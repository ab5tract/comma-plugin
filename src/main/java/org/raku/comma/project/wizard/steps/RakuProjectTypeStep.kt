package org.raku.comma.project.wizard.steps

import com.intellij.CommonBundle
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.Messages
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.*
import org.raku.comma.project.wizard.RakuModuleWizardBuilder
import org.raku.comma.sdk.RakuSdkUtil
import org.raku.comma.services.application.RakuSdkStore
import org.raku.comma.services.application.RakuSdkStoreEntry
import org.raku.comma.utils.RakuProjectType
import javax.swing.*

class RakuProjectTypeStep(private val builder: RakuModuleWizardBuilder) : ModuleWizardStep()  {

    // SDK objects
    private val sdkComboBox = ComboBox<RakuSdkStoreEntry>()
    private val addSdkButton: JButton
    private var hasSdkOptions: Boolean = false

    // Project Type objects
    private val projectTypeList = JBList<String>()
    private val projectTypeDescription: JBLabel = JBLabel()

    init {
        service<RakuSdkStore>()

        addSdkButton = JButton("Add Raku SDK")
        addSdkButton.addActionListener { addSdk() }

        projectTypeList.setListData(RakuProjectType.typeLabels())
        projectTypeList.addListSelectionListener {
            if (! it.valueIsAdjusting) {
                val projectType = RakuProjectType.fromTypeLabel(projectTypeList.selectedValue)
                builder.rakuProjectType = projectType
                projectTypeDescription.text = "<html>%s</html>".format(RakuProjectType.getDescription(projectType))
            }
        }
        projectTypeList.setSelectedValue(RakuProjectType.typeLabels().firstElement(), false)
    }

    override fun getComponent(): JComponent {
        loadSdks()

        val sdkPanel = panel {
            indent {
                row {
                    cell(sdkComboBox)
                    cell(addSdkButton)
                }.topGap(TopGap.MEDIUM).bottomGap(BottomGap.MEDIUM)
            }
        }
        sdkPanel.border = builder.makeBorder("Select Raku SDK")

        val projectTypePanel = panel {
            indent {
                row {
                    cell(projectTypeList)
                        .align(AlignX.LEFT)
                }.topGap(TopGap.SMALL)
            }
        }
        projectTypePanel.border = builder.makeBorder("Select Raku Project Type")


        val projectDescriptionPanel = panel {
            indent {
                row {
                    cell(projectTypeDescription) //.align(AlignY.CENTER)
                }
            }
        }
        projectDescriptionPanel.border = builder.makeBorder("Project Type Description")
        projectDescriptionPanel.withMinimumHeight(160)

        val finalPanel = panel {
            separator(background = JBColor.background()).topGap(TopGap.MEDIUM).bottomGap(BottomGap.MEDIUM)
            row {
                cell(sdkPanel).align(Align.FILL)
            }.topGap(TopGap.MEDIUM)
            separator(background = JBColor.background()).topGap(TopGap.MEDIUM).bottomGap(BottomGap.MEDIUM)
            row {
                cell(projectTypePanel).align(Align.FILL)
            }.topGap(TopGap.SMALL)
            separator(background = JBColor.background()).topGap(TopGap.MEDIUM).bottomGap(BottomGap.MEDIUM)
            row {
                cell(projectDescriptionPanel).align(Align.FILL)
            }.topGap(TopGap.SMALL)
        }

        return finalPanel
    }

    override fun updateDataModel() {
        builder.sdkPath = (sdkComboBox.selectedItem as? RakuSdkStoreEntry)?.path
        builder.ensureProjectType(RakuProjectType.fromTypeLabel(projectTypeList.selectedValue))
    }

    override fun validate(): Boolean {
        if (sdkComboBox.selectedItem == null) {
            if (Messages.showDialog(
                    noSdkMessage,
                    "No SDK Specified",
                    arrayOf(CommonBundle.getYesButtonText(), CommonBundle.getNoButtonText()),
                    1,
                    Messages.getWarningIcon()
                ) != Messages.YES
            ) {
                return false
            }
        }
        return true
    }

    private fun addSdk() {
        FileChooser.chooseFile(RakuSdkUtil.fileChooserDescriptor(), null, null, { file ->
            if (RakuSdkUtil.isValidRakuSdkHome(file.path)) {
                val sdkStore = service<RakuSdkStore>()
                val added = sdkStore.addSdk(file.path)
                sdkComboBox.addItem(added)
                sdkComboBox.selectedItem = added
                builder.sdkPath = added.path
                if (!sdkComboBox.isEnabled) sdkComboBox.setEnabled(true)
            } else {
                Messages.showInfoMessage(
                    null as Project?,
                    selectedFolderIsNotRakuSdk,
                    "No Raku SDK Found"
                )
            }
        })
    }

    private fun loadSdks() {
        val sdkStore = service<RakuSdkStore>()
        if (sdkStore.sdks.isNotEmpty()) {
            sdkStore.sdks.forEach { sdkComboBox.addItem(it) }
            sdkStore.sdks.maxByOrNull { it.version }!!.let { sdkComboBox.selectedItem = it }
            hasSdkOptions = true
        } else {
            hasSdkOptions = false
        }
        sdkComboBox.setEnabled(hasSdkOptions)
    }

    private val noSdkMessage: String
        get() = "Do you want to create a project with no SDK assigned?\nAn SDK is required for compiling, debugging and running applications,\nas well as for the standard SDK symbols resolution."

    private val selectedFolderIsNotRakuSdk: String
        get() = "The selected folder does not contain any recognizable Raku binaries."
}