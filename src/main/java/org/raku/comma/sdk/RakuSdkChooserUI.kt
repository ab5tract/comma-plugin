package org.raku.comma.sdk

import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.components.JBComboBoxLabel
import com.intellij.ui.dsl.builder.*
import org.raku.comma.services.application.RakuSdkStore
import org.raku.comma.services.application.RakuSdkStoreEntry
import org.raku.comma.services.project.RakuProjectDetailsService
import org.raku.comma.services.project.RakuProjectSdkService
import java.awt.event.ActionEvent
import javax.swing.JComponent

class RakuSdkChooserUI(
    private val project: Project,
    private var currentSdkHome: String? = null
) : DialogWrapper(false) {

    private val hasSdkOptions: Boolean

    private val sdkComboBox = ComboBox<RakuSdkStoreEntry>()
    private val comboBoxLabel = JBComboBoxLabel()

    private var comboBoxCell: Cell<*>? = null

    init {
        if (currentSdkHome == null) {
            currentSdkHome = project.getService(RakuProjectSdkService::class.java).sdkPath
        }

        val sdkStore = service<RakuSdkStore>()
        val sdkService = project.service<RakuProjectSdkService>()

        if (sdkStore.sdks.isNotEmpty()) {
            sdkStore.sdks.forEach { sdkComboBox.addItem(it) }
            if (sdkService.sdkPath != null) {
                sdkStore.sdks.firstOrNull { it.path == sdkService.sdkPath }?.let { sdkComboBox.selectedItem = it }
            }
            hasSdkOptions = true
        } else {
            hasSdkOptions = false
        }

        comboBoxLabel.text = "Select Raku SDK: "
        comboBoxLabel.icon = null

        title = "Choose Raku SDK"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel: DialogPanel = panel {
            row {
                comboBoxCell = cell(sdkComboBox).enabled(hasSdkOptions)
                button("Add SDK", { addSdk(it) })
            }.topGap(TopGap.SMALL)
                .bottomGap(BottomGap.MEDIUM)
        }.withMinimumWidth(280)
        return panel
    }

    private fun addSdk(event: ActionEvent) {
        comboBoxCell!!.enabled(true)

        FileChooser.chooseFile(RakuSdkUtil.fileChooserDescriptor(), project, null, { file ->
            if (RakuSdkUtil.isValidRakuSdkHome(file.path)) {
                val sdkStore = service<RakuSdkStore>()
                val added = sdkStore.addSdk(file.path)
                sdkComboBox.addItem(added)
                sdkComboBox.selectedItem = added
            } else {
                val location = sdkComboBox.locationOnScreen
                location.translate(16, 32)
                JBPopupFactory.getInstance()
                              .createMessage("The selected folder must contain Raku binaries")
                              .show(RelativePoint(location))
            }
        })
    }

    override fun doOKAction() {
        val sdkService: RakuProjectSdkService = project.getService(RakuProjectSdkService::class.java)
        val sdkHome = (sdkComboBox.selectedItem as RakuSdkStoreEntry).path

        if (RakuSdkUtil.isValidRakuSdkHome(sdkHome)) sdkService.setProjectSdkPath(sdkHome)

        super.doOKAction()
    }

    override fun doCancelAction() {
        project.service<RakuProjectDetailsService>().hasProjectSdkPrompted = true
        super.doCancelAction()
    }
}