package org.raku.comma.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.selected
import com.intellij.util.ui.CheckBox
import com.intellij.util.ui.JBUI
import org.raku.comma.RakuIcons
import org.raku.comma.language.RakuLanguageVersionService
import org.raku.comma.module.builder.RakuModuleBuilderModule
import org.raku.comma.module.builder.RakuModuleBuilderScript
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.swing.BorderFactory
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.border.Border
import javax.swing.border.EtchedBorder.LOWERED
import javax.swing.event.ListSelectionEvent

class NewRakuFileAction : AnAction(), DumbAware {
    override fun actionPerformed(event: AnActionEvent) {
        val project: Project = event.project ?: return

        val virtualFile: VirtualFile? = event.getData(CommonDataKeys.VIRTUAL_FILE)

        val defaultPath: String = if (virtualFile == null || !virtualFile.isDirectory) ""
                                  else virtualFile.path

        val dialogOutput = CompletableFuture<DialogOutput>()

        if (RakuFileCreationDialog(project, defaultPath, dialogOutput).showAndGet()) {
            // TODO: Notify the user that the file could not be created
            if (! dialogOutput.isCompletedExceptionally) {
                val output = dialogOutput.get()
                val finalPath = output.createdPath
                val finalFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(Paths.get(finalPath).toFile())

                if (finalFile != null && output.openIt) {
                    OpenFileDescriptor(project, finalFile).navigate(true)
                }
            }
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}

private class RakuFileCreationDialog(
    private val project: Project,
    private val path: String,
    private val createdFile: CompletableFuture<DialogOutput>
) : DialogWrapper(false) {

    private val fileTypeChooser = JBList<RakuFileChoice>()
    private var nameField: Cell<JBTextField>? = null
    private var openAfterwards: Cell<JBCheckBox>? = null
    private var unitScoped: Cell<JBCheckBox>? = null
    private var addSubMain: Cell<JBCheckBox>? = null

    private val languageVersion = project.service<RakuLanguageVersionService>().version
    private val moduleBuilderTypes = rakuFileChoices.map { it.choiceName }.filter { it != "Script" }.toSet()

    init {
        fileTypeChooser.setListData(Vector(rakuFileChoices))
        fileTypeChooser.cellRenderer = RakuFileChoiceCellRenderer()
        fileTypeChooser.addListSelectionListener(this::listListener)

        init()
        isOKActionEnabled = false
    }

    override fun createCenterPanel(): JComponent {
        val chooserPanel: DialogPanel = panel {
            row {
                cell(fileTypeChooser).align(Align.FILL)
            }.topGap(TopGap.SMALL).bottomGap(BottomGap.SMALL)
        }
        chooserPanel.border = makeBorder("Options")

        val selectionPanel: DialogPanel = panel {
            row {
                nameField = textField().label("Name ", LabelPosition.LEFT)
            }
        }
        nameField!!.component.addKeyListener(keyListener())

        val optionsPanel: DialogPanel = panel {
            row {
                openAfterwards = checkBox("Open after creation?")
                unitScoped = checkBox("Declare in 'unit' scope?")
            }.layout(RowLayout.LABEL_ALIGNED)
            row {
                placeholder()
                addSubMain = checkBox("Add sub MAIN?")
            }.layout(RowLayout.LABEL_ALIGNED)
        }
        openAfterwards!!.component.isSelected = true
        openAfterwards!!.component.border = JBUI.Borders.emptyLeft(16)
        optionsPanel.border = makeBorder("Options")

        // Could this be accomplished via the ComponentPredicate system?
        addSubMain!!.component.addActionListener({ _ ->
            unitScoped!!.enabled(addSubMain!!.component.isSelected)
        })
        addSubMain!!.enabled(false)
        unitScoped!!.enabled(false)
        unitScoped!!.component.border = JBUI.Borders.emptyRight(16)


        return panel {
            row {
                cell(selectionPanel).align(Align.CENTER)
            }.bottomGap(BottomGap.MEDIUM)

            row {
                cell(chooserPanel).align(Align.CENTER)
            }.topGap(TopGap.MEDIUM).bottomGap(BottomGap.MEDIUM)

            row {
                cell(optionsPanel)
            }.topGap(TopGap.MEDIUM).bottomGap(BottomGap.MEDIUM)
        }
    }

    private fun keyListener(): KeyListener {
        return object: KeyListener {
            override fun keyReleased(e: KeyEvent?) {
                if (! nameField!!.component.text.isNullOrEmpty() && fileTypeChooser.selectedValue != null) {
                    isOKActionEnabled = true
                }
            }
            override fun keyPressed(e: KeyEvent?) {}
            override fun keyTyped(e: KeyEvent?) {}
        }
    }

    private fun listListener(event: ListSelectionEvent) {
        if (event.valueIsAdjusting) return
        if (nameField == null) return

        if (! nameField!!.component.text.isNullOrEmpty()) {
            isOKActionEnabled = true
        }

        val selected = fileTypeChooser.selectedValue
        val scriptSelected = selected.choiceName == "Script"
        addSubMain!!.enabled(scriptSelected)
        unitScoped!!.enabled(!scriptSelected || addSubMain!!.component.isSelected)
    }

    override fun doOKAction() {
        val thePath = Path.of(path)
        val name = nameField!!.component.text
        val selected = fileTypeChooser.selectedValue

        val shouldOpen = openAfterwards!!.component.isSelected
        val isUnitScope = unitScoped!!.component.isSelected

        val path: String? =
            when (selected.choiceName) {
                "Script" -> {
                    val scriptName = "%s.%s".format(name, selected.extension)
                    RakuModuleBuilderScript.stubScript(thePath, scriptName, true, languageVersion)
                }

                in moduleBuilderTypes -> RakuModuleBuilderModule.stubModule(project,
                                                                            thePath,
                                                                            name,
                                                                            false,
                                                                            shouldOpen,
                                                                            null,
                                                                            selected.choiceName,
                                                                            isUnitScope,
                                                                            languageVersion)

                else -> null
            }

        if (path != null) {
            createdFile.complete(DialogOutput(path, openAfterwards!!.component.isSelected))
        } else {
            createdFile.completeExceptionally(IOException("Unable to create file"))
        }
        super.doOKAction()
    }

    fun makeBorder(text: String): Border {
        return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(LOWERED), text)
    }
}

private val rakuFileChoices = listOf(
    RakuFileChoice(RakuIcons.MODULE, "Module"),
    RakuFileChoice(RakuIcons.CLASS, "Class"),
    RakuFileChoice(RakuIcons.ROLE, "Role"),
    RakuFileChoice(RakuIcons.SUB, "Script", "raku"),
    RakuFileChoice(RakuIcons.GRAMMAR, "Grammar"),
    RakuFileChoice(RakuIcons.PACKAGE, "Package"),
// TODO: Make an icon and add the rakudoc option
//    RakuFileChoice(RakuIcons.PACKAGE, "Documentation"),
)

private data class RakuFileChoice(val choiceIcon: Icon, val choiceName: String, val extension: String = "rakumod")
private data class DialogOutput(val createdPath: String, val openIt: Boolean)

private class RakuFileChoiceCellRenderer : ColoredListCellRenderer<RakuFileChoice>() {
    override fun customizeCellRenderer(
        list: JList<out RakuFileChoice>,
        value: RakuFileChoice?,
        index: Int,
        selected: Boolean,
        hasFocus: Boolean
    ) {
        if (value == null) return

        icon = value.choiceIcon
        append("  %s".format(value.choiceName), SimpleTextAttributes.REGULAR_ATTRIBUTES)
        ipad = JBUI.insets(0, 24)
    }
}
