// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.raku.project.projectWizard.components

import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.components.StorageScheme
import com.intellij.openapi.options.ConfigurationException
import com.intellij.platform.templates.TemplateModuleBuilder
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import org.jetbrains.annotations.TestOnly
import org.raku.project.projectWizard.NewProjectNameLocationSettings
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import javax.swing.*

/**
 * @author Dmitry Avdeev
 */
class CommaProjectSettingsStep(private val myWizardContext: WizardContext) : ModuleWizardStep(), SettingsStep {
    private var mySettingsPanel: JPanel? = null

    private var myExpertPanel: JPanel? = null

    private var myNamePathComponent: NamePathComponent = NamePathComponent.initNamePathComponent(
        myWizardContext
    )
    private var myFormatPanel = ProjectFormatPanel()

    private var myPanel: JPanel? = null
    private var myModuleNameLocationComponent: ModuleNameLocationComponent? = null

    @get:TestOnly
    var settingsStep: ModuleWizardStep? = null
        private set

    init {
        myNamePathComponent.setShouldBeAbsolute(true)
        val modulePanel = modulePanel
        if (myWizardContext.isCreatingNewProject) {
            mySettingsPanel!!.add(myNamePathComponent, BorderLayout.NORTH)
            addExpertPanel(modulePanel)
        } else {
            mySettingsPanel!!.add(modulePanel, BorderLayout.NORTH)
        }
        myModuleNameLocationComponent!!.bindModuleSettings(myNamePathComponent)


        myExpertPanel!!.border = JBUI.Borders.empty(0, IdeBorderFactory.TITLED_BORDER_INDENT, 5, 0)

        if (myWizardContext.isCreatingNewProject) {
            addProjectFormat(modulePanel)
        }
    }

    private val modulePanel: JPanel
        get() = myModuleNameLocationComponent!!.modulePanel

    private val nameComponent: JTextField
        get() = if (myWizardContext.isCreatingNewProject) myNamePathComponent.nameComponent else myModuleNameLocationComponent!!.moduleNameField

    private fun addProjectFormat(panel: JPanel) {
        addField("Project \u001bformat:", myFormatPanel.storageFormatComboBox, panel)
    }

    override fun getHelpId(): String {
        return if (myWizardContext.isCreatingNewProject) "New_Project_Main_Settings" else "Add_Module_Main_Settings"
    }

    private fun setupPanels() {
        val moduleBuilder = myWizardContext.projectBuilder as ModuleBuilder?
        restorePanel(myNamePathComponent, 4)
        restorePanel(modulePanel, if (myWizardContext.isCreatingNewProject) 8 else 6)
        restorePanel(myExpertPanel!!, if (myWizardContext.isCreatingNewProject) 1 else 0)
        settingsStep = moduleBuilder?.modifySettingsStep(this)

        for (i in 0..5) {
            modulePanel.getComponent(i).isVisible = moduleBuilder !is EmptyModuleBuilder
        }

        mySettingsPanel!!.revalidate()
        mySettingsPanel!!.repaint()
    }

    override fun updateStep() {
        setupPanels()
    }

    @Throws(ConfigurationException::class)
    override fun validate(): Boolean {
        if (myWizardContext.isCreatingNewProject) {
            if (!myNamePathComponent.validateNameAndPath(myWizardContext, myFormatPanel.isDefault)) return false
        }

        if (!myModuleNameLocationComponent!!.validate()) return false

        if (settingsStep != null) {
            return settingsStep!!.validate()
        }
        return true
    }

    override fun getComponent(): JComponent {
        return myPanel!!
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return nameComponent
    }

    override fun updateDataModel() {
        myWizardContext.projectName = myNamePathComponent.nameValue
        myWizardContext.setProjectFileDirectory(myNamePathComponent.path)
        myFormatPanel.updateData(myWizardContext)

        myModuleNameLocationComponent!!.updateDataModel()

        val moduleBuilder = myWizardContext.projectBuilder
        if (moduleBuilder is TemplateModuleBuilder) {
            myWizardContext.projectStorageFormat = StorageScheme.DIRECTORY_BASED
        }

        if (settingsStep != null) {
            settingsStep!!.updateDataModel()
        }
    }

    override fun getName(): String {
        return "Project Settings"
    }

    override fun getContext(): WizardContext {
        return myWizardContext
    }

    override fun addSettingsField(label: String, field: JComponent) {
        val panel = if (myWizardContext.isCreatingNewProject) myNamePathComponent else modulePanel
        addField(label, field, panel)
    }

    override fun addSettingsComponent(component: JComponent) {
        val panel = if (myWizardContext.isCreatingNewProject) myNamePathComponent else modulePanel
        panel.add(
            component, GridBagConstraints(
                0, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, JBUI.emptyInsets(), 0, 0
            )
        )
    }

    override fun addExpertPanel(panel: JComponent) {
        myExpertPanel!!.add(
            panel, GridBagConstraints(
                0, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL, JBUI.emptyInsets(), 0, 0
            )
        )
    }

    override fun addExpertField(label: String, field: JComponent) {
        val panel = if (myWizardContext.isCreatingNewProject) modulePanel else myExpertPanel!!
        addField(label, field, panel)
    }

    @Deprecated("this code is borrowed from intellij-community")
    override fun getModuleNameField(): JTextField {
        return nameComponent
    }

    override fun getModuleNameLocationSettings(): ModuleNameLocationSettings? {
        return if (myWizardContext.isCreatingNewProject)
            NewProjectNameLocationSettings(myNamePathComponent, myModuleNameLocationComponent)
        else
            myModuleNameLocationComponent
    }

    @TestOnly
    fun setPath(path: String?) {
        myNamePathComponent.path = path
    }

    @TestOnly
    fun setNameValue(name: String?) {
        myNamePathComponent.nameValue = name
    }

    override fun getIcon(): Icon? {
        return null
    }

    private fun createUIComponents() {
        myModuleNameLocationComponent = ModuleNameLocationComponent(myWizardContext)
    }

    fun setModuleName(moduleName: String) {
        myModuleNameLocationComponent!!.moduleName = moduleName
    }

    fun bindModuleSettings() {
        myModuleNameLocationComponent!!.bindModuleSettings(myNamePathComponent)
    }

    companion object {
        private fun restorePanel(component: JPanel, i: Int) {
            while (component.componentCount > i) {
                component.remove(component.componentCount - 1)
            }
        }

        @JvmStatic
        fun addField(label: String, field: JComponent, panel: JPanel) {
            val jLabel: JLabel = JBLabel(label)
            jLabel.labelFor = field
            jLabel.verticalAlignment = SwingConstants.TOP
            panel.add(
                jLabel, GridBagConstraints(
                    0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.VERTICAL, JBUI.insets(5, 0, 5, 0), 4, 0
                )
            )
            panel.add(
                field, GridBagConstraints(
                    1, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, JBUI.insetsBottom(5), 0, 0
                )
            )
        }
    }
}
