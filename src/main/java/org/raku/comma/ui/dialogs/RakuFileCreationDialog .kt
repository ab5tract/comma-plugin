package org.raku.comma.ui.dialogs

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.util.minimumHeight
import com.intellij.ui.util.minimumWidth
import com.intellij.util.ui.JBUI
import org.raku.comma.RakuIcons
import org.raku.comma.language.RakuLanguageVersionService
import org.raku.comma.module.builder.RakuModuleBuilderModule
import org.raku.comma.module.builder.RakuModuleBuilderScript
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.IOException
import java.nio.file.Path
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.swing.*
import javax.swing.border.Border
import javax.swing.border.EtchedBorder.LOWERED
import javax.swing.event.ChangeListener
import javax.swing.event.ListSelectionEvent

// This opt-in is required for toHexString()
@SuppressWarnings("deprecated")
class RakuFileCreationDialog(
    private val project: Project,
    private val path: String,
    private val createdFile: CompletableFuture<DialogOutput>
) : DialogWrapper(false) {

    private val fileTypeChooser = JBList<RakuFileChoice>()
    private var nameField: Cell<JBTextField>? = null
    private var validationIssues: Cell<JEditorPane>? = null
    private var relativeToLib: Cell<JBCheckBox>? = null
    private var relativeToLibComment: Cell<JEditorPane>? = null
    private var openAfterwards: Cell<JBCheckBox>? = null
    private var unitScoped: Cell<JBCheckBox>? = null
    private var addSubMain: Cell<JBCheckBox>? = null

    // TODO: Provide a specific color to the theme.. but for now, this yellow (fallback?) is really readable
    // (Needs to be tested in the light theme)
    @OptIn(ExperimentalStdlibApi::class)
    private val errorColor = "#" + JBColor.namedColor("Label.errorForeground").rgb.toHexString()
    private val languageVersion = project.service<RakuLanguageVersionService>().version
    private val moduleBuilderTypes = rakuFileChoices.map { it.choiceName }.filter { it != "Script" }.toSet()

    // Why can't I get the color style to work consistently?
    private var emptySpaceComment: String =
            """
                <b></b>
                <ul style='list-style-type:none'>
                    <li></li>
                    <li></li>
                </ul>
            """

    private val pathRelative: String
    private val adjustedPath: String
        get() = adjustPath()

    init {
        fileTypeChooser.setListData(Vector(rakuFileChoices))
        fileTypeChooser.cellRenderer = RakuFileChoiceCellRenderer()
        fileTypeChooser.addListSelectionListener(this::listListener)
        fileTypeChooser.visibleRowCount = 5
        fileTypeChooser.layoutOrientation = JList.VERTICAL_WRAP

        // If there is no "/lib/", it means the user has selected the root of the project
        val splitted = path.split("/lib/")
        pathRelative = if (1 < splitted.size) "./lib/${splitted.last()}/" else "./"

        init()
        isOKActionEnabled = false
    }

    override fun createCenterPanel(): JComponent {
        val createRelativePanel: DialogPanel = panel {
            row {
                relativeToLib = checkBox("Create from ./lib/").selected(true).align(Align.CENTER)
                relativeToLibComment = comment("./lib/") .align(Align.CENTER)
            }.layout(RowLayout.LABEL_ALIGNED)
        }
        relativeToLib!!.component.toolTipText =
            "File is created relative to the ./lib/ of the project or relative to the selected path ($pathRelative)"
        relativeToLib!!.component.addChangeListener(relativeToLibListener())
        createRelativePanel.isVisible = path.isNotEmpty()

        val namePanel: DialogPanel = panel {
            row {
                nameField = textField().label("Name ", LabelPosition.LEFT).align(Align.CENTER)
            }
        }

        val validationPanel: DialogPanel = panel {
            row {
                validationIssues = comment(emptySpaceComment)
            }
        }
        nameField!!.component.addKeyListener(keyListener())
        validationIssues!!.component.contentType = "text/html"

        val chooserPanel: DialogPanel = panel {
            row {
                cell(fileTypeChooser).align(Align.FILL)
            }.topGap(TopGap.SMALL).bottomGap(BottomGap.SMALL)
        }
        chooserPanel.border = makeBorder("Options")

        val optionsPanel: DialogPanel = panel {
            row {
                openAfterwards = checkBox("Open after creation")
                unitScoped = checkBox("Declare in 'unit' scope")
            }.layout(RowLayout.LABEL_ALIGNED)
            row {
                placeholder()
                addSubMain = checkBox("Add sub MAIN")
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

        val finalPanel = panel {
            row { cell(namePanel).align(Align.CENTER) }
            row { cell(createRelativePanel).align(Align.CENTER) }.bottomGap(BottomGap.MEDIUM)
            row { cell(validationPanel).align(Align.CENTER) }.topGap(TopGap.SMALL).bottomGap(BottomGap.MEDIUM)
            row { cell(chooserPanel).align(Align.CENTER) }.topGap(TopGap.MEDIUM).bottomGap(BottomGap.MEDIUM)
            row { cell(optionsPanel).align(Align.CENTER) }.topGap(TopGap.MEDIUM).bottomGap(BottomGap.MEDIUM)
        }
        finalPanel.minimumWidth = 560
        finalPanel.minimumHeight = 420

        return finalPanel
    }

    private fun keyListener(): KeyListener {
        return object : KeyListener {
            override fun keyReleased(e: KeyEvent?) {
                if (!nameField!!.component.text.isNullOrEmpty() && fileTypeChooser.selectedValue != null) {
                    isOKActionEnabled = true
                } else {
                    isOKActionEnabled = false
                }
                validateCurrentState()
                updateOutputPath()
            }
            override fun keyPressed(e: KeyEvent?) {}
            override fun keyTyped(e: KeyEvent?) {}
        }
    }

    private fun validateCurrentState() {
        try {
            val currentName = nameField!!.component.text
            // this will throw
            pathFromCurrentState(currentName, fileTypeChooser.selectedValue ?: rakuFileChoices.first())
            val validator = RakuModuleNameValidator(currentName)
            if (validator.invalid) {
                throw IllegalStateException(validator.validation.problems.joinToString("\n"))
            }

            // Should only run if we are without errors
            validationIssues!!.component.text = emptySpaceComment
            relativeToLibComment!!.component.isVisible = true
            if (! nameField!!.component.text.isNullOrEmpty() && fileTypeChooser.selectedValue != null) {
                isOKActionEnabled = true
            }
        } catch (e: Exception) {
            val splitted = e.message!!.split("\n")
            val html = """
                <b>Module name validation errors</b>
                <ul>
                    ${splitted.joinToString("\n") { "<li style='color:$errorColor;'>$it</li>" }}
                    ${if (splitted.size == 1) "<li style='list-style-type:none'></li>" else "" }
                </ul>
            """

            val component = validationIssues!!.component
            component.text = html
            validationIssues!!.component.isVisible = true
            relativeToLibComment!!.component.isVisible = false
            isOKActionEnabled = false
        }
    }

    private fun listListener(event: ListSelectionEvent) {
        if (event.valueIsAdjusting) return
        if (nameField == null) return

        if (! nameField!!.component.text.isNullOrEmpty()) {
            isOKActionEnabled = true
            updateOutputPath()
        }

        val selected = fileTypeChooser.selectedValue
        val scriptSelected = selected.choiceName == "Script"
        addSubMain!!.enabled(scriptSelected)
        unitScoped!!.enabled(!scriptSelected || addSubMain!!.component.isSelected)
    }

    private fun relativeToLibListener(): ChangeListener {
        return ChangeListener {
            updateOutputPath()
        }
    }

    private fun updateOutputPath() {
        val currentText: String = nameField!!.component.text
        val suffix =    if (fileTypeChooser.selectedValue != null)
                            fileTypeChooser.selectedValue.suffix
                        else ""

        var endColonsIgnored = currentText
        while (endColonsIgnored.endsWith(":")) {
            endColonsIgnored = endColonsIgnored.substring(0, endColonsIgnored.length - 1)
        }
        val prefix = if (relativeToLib!!.component.isSelected) "./lib/" else pathRelative

        relativeToLibComment!!.text(makePathString(prefix, endColonsIgnored, suffix))
    }

    private fun adjustPath(): String {
        if (path.isEmpty()) return "${project.basePath}/lib/"
        return  if (relativeToLib!!.component.isSelected)
                    "${path.split("/lib/").first()}/lib/"
                else path
    }

    private fun makePathString(prefix: String, fileName: String, suffix: String): String {
        return "$prefix${fileName.replace("::", "/")}$suffix"
    }

    // Currently only used for testing whether a file is creatable
    private fun pathFromCurrentState(currentName: String, currentType: RakuFileChoice): Path {
        return Path.of(makePathString(adjustedPath, currentName, currentType.suffix))
    }

    override fun doOKAction() {
        val thePath = Path.of(adjustedPath)
        val name = nameField!!.component.text
        val selected = fileTypeChooser.selectedValue

        val shouldOpen = openAfterwards!!.component.isSelected
        val isUnitScope = unitScoped!!.component.isSelected

        val outputPath: String? =
            when (selected.choiceName) {
                "Script"              -> {
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

                else                  -> null
            }

        if (outputPath != null) {
            createdFile.complete(DialogOutput(outputPath, openAfterwards!!.component.isSelected))
        } else {
            createdFile.completeExceptionally(IOException("Unable to create file"))
        }
        super.doOKAction()
    }

    private fun makeBorder(text: String): Border {
        return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(LOWERED), text)
    }
}

class RakuModuleNameValidator(val wantedName: String) {
    val illegal: Regex = Regex("%|\\$|#|@|\\^|&|\\*|!|\\p{C}|\\s")
    val numbers: Regex = Regex("\\p{Digit}")

    val validation = validate()

    val valid: Boolean = validation.valid
    val invalid: Boolean = !valid

    // It's easier to write the negative case
    private fun validate(): NameValidationResult {
        val problems: MutableList<String> = mutableListOf()
        if (illegal.containsMatchIn(wantedName)) problems.add("Name contains an unsupported character")
        val startsWithNumber = wantedName.split("::").any { numbers.matchesAt(it, 0) }
        if (startsWithNumber) problems.add("Name part starts with number")

        return NameValidationResult(problems.isEmpty(), problems)
    }
}

data class NameValidationResult(val valid: Boolean, val problems: List<String>)

private val rakuFileChoices = listOf(
    RakuFileChoice(RakuIcons.MODULE, "Module"),
    RakuFileChoice(RakuIcons.CLASS, "Class"),
    RakuFileChoice(RakuIcons.ROLE, "Role"),
    RakuFileChoice(RakuIcons.SUB, "Script", "raku"),
    RakuFileChoice(RakuIcons.GRAMMAR, "Grammar"),
    RakuFileChoice(RakuIcons.PACKAGE, "Package"),
    RakuFileChoice(RakuIcons.MODULE, "Model"),
    RakuFileChoice(RakuIcons.MODULE, "Monitor"),
// TODO: Make an icon and add the rakudoc option
//    RakuFileChoice(RakuIcons.PACKAGE, "Documentation"),
)

private data class RakuFileChoice(val choiceIcon: Icon, val choiceName: String, val extension: String = "rakumod") {
    override fun toString(): String = choiceName
    val suffix: String = ".$extension"
}

data class DialogOutput(val createdPath: String, val openIt: Boolean)

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