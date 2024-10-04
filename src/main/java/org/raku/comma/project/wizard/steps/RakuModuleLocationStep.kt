package org.raku.comma.project.wizard.steps

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.util.ui.UIUtil
import org.raku.comma.project.wizard.RakuModuleWizardBuilder
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import javax.swing.JComponent

class RakuModuleLocationStep(private val builder: RakuModuleWizardBuilder) : ModuleWizardStep() {

    private var projectName: Cell<JBTextField>? = null
    private var projectPath: Cell<JBTextField>? = null

    override fun getComponent(): JComponent {
        val projectNamePanel = panel {
            indent {
                row {
                    projectName = textField().text(builder.safeModuleName ?: "").align(Align.FILL)
                }.topGap(TopGap.SMALL).bottomGap(BottomGap.SMALL)
            }
        }
        projectNamePanel.border = builder.makeBorder("Project Name")

        val projectPathPanel = panel {
            indent {
                row {
                    projectPath = textField().text("~/CommaProjects/" + projectName?.component?.text).align(Align.FILL)
                }.topGap(TopGap.SMALL).bottomGap(BottomGap.SMALL)
            }
        }
        projectPathPanel.border = builder.makeBorder("Project Path")

        addKeyListener()

        val finalPanel = panel {
            separator(background = UIUtil.getPanelBackground()).topGap(TopGap.MEDIUM).bottomGap(BottomGap.MEDIUM)
            row {
                cell(projectNamePanel).align(Align.FILL)
            }
            separator(background = UIUtil.getPanelBackground()).bottomGap(BottomGap.MEDIUM)
            row {
                cell(projectPathPanel).align(Align.FILL)
            }
        }

        return finalPanel
    }

    private fun addKeyListener() {
        projectName!!.component.addKeyListener(object: KeyListener {
            override fun keyReleased(e: KeyEvent?) {
                val splitted = projectPath!!.component.text.split(File.separator).toMutableList()
                splitted[splitted.lastIndex] = projectName!!.component.text
                projectPath!!.component.text = splitted.joinToString(File.separator)
            }
            override fun keyPressed(e: KeyEvent?) {}
            override fun keyTyped(e: KeyEvent?) {}
        })
    }

    override fun updateDataModel() {
        if (projectPath == null || projectName == null) return

        builder.projectPath = projectPath!!.component.text
        builder.projectName = projectName!!.component.text
    }
}