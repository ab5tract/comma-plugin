package org.raku.comma.project.wizard.steps

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.util.ui.UIUtil
import org.raku.comma.project.wizard.RakuModuleWizardBuilder
import org.raku.comma.utils.RakuProjectType
import javax.swing.JComponent

class RakuModuleNameStep(private val builder: RakuModuleWizardBuilder) : ModuleWizardStep() {

    private var moduleNameTextField: Cell<JBTextField>? = null
    private var entryPointTextField: Cell<JBTextField>? = null

    override fun getComponent(): JComponent {
        val namePanel = panel {
            indent {
                row {
                    moduleNameTextField = textField().align(Align.FILL)
                }.topGap(TopGap.SMALL).bottomGap(BottomGap.SMALL)
            }
        }
        namePanel.border = builder.makeBorder("Module Name")

        val entryPointPanel = panel {
            indent {
                row {
                    entryPointTextField = textField().align(Align.FILL)
                }.topGap(TopGap.SMALL)
            }
        }
        entryPointPanel.border = builder.makeBorder("Entry Point")
        entryPointPanel.isVisible = builder.rakuProjectType == RakuProjectType.RAKU_APPLICATION

        val finalPanel = panel {
            separator(background = UIUtil.getPanelBackground()).topGap(TopGap.MEDIUM).bottomGap(BottomGap.MEDIUM)
            row {
                cell(namePanel).align(Align.FILL)
            }
            separator(background = UIUtil.getPanelBackground()).topGap(TopGap.SMALL).bottomGap(BottomGap.SMALL)
            row {
                cell(entryPointPanel).align(Align.FILL)
            }
        }
        return finalPanel
    }

    override fun updateDataModel() {
        builder.ensureModuleName(moduleNameTextField!!.component.text)

        if (builder.rakuProjectType == RakuProjectType.RAKU_APPLICATION) {
            builder.ensureEntryPoint(entryPointTextField!!.component.text)
        }
    }
}