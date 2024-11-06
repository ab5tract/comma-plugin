package org.raku.comma.ui.editorMenu

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.*
import org.raku.comma.services.application.RakuSdkStore
import org.raku.comma.services.application.RakuSdkStoreEntry
import org.raku.comma.services.project.RakuProjectSdkService
import javax.swing.Icon

class RakuSdkListPopupStep(private val project: Project) : ListPopupStep<RakuSdkStoreEntry> {
    override fun getTitle(): String? {
        return "Available Raku SDKs"
    }

    override fun canceled() {
        return
    }

    override fun isMnemonicsNavigationEnabled(): Boolean {
        return false
    }

    override fun getMnemonicNavigationFilter(): MnemonicNavigationFilter<RakuSdkStoreEntry>? {
        return null
    }

    override fun isSpeedSearchEnabled(): Boolean {
        return false
    }

    override fun getSpeedSearchFilter(): SpeedSearchFilter<RakuSdkStoreEntry>? {
        return null
    }

    override fun isAutoSelectionEnabled(): Boolean {
        return false
    }

    override fun getFinalRunnable(): Runnable? {
        return null
    }

    override fun getValues(): MutableList<RakuSdkStoreEntry> {
        return service<RakuSdkStore>().sdks.toMutableList()
    }

    override fun getDefaultOptionIndex(): Int {
        val current = values.firstOrNull { it.path == project.service<RakuProjectSdkService>().sdkPath } ?: return -1
        return current.index
    }

    override fun getSeparatorAbove(aboveThis: RakuSdkStoreEntry): ListSeparator? {
        return null
    }

    override fun getTextFor(item: RakuSdkStoreEntry): String {
        val sdkString = item.toString()
        return if (sdkString.length > 23) sdkString.substring(0, 23) + "..." else sdkString
    }

    override fun getIconFor(item: RakuSdkStoreEntry): Icon? {
        return null
    }

    override fun isSelectable(item: RakuSdkStoreEntry): Boolean {
        return true
    }

    override fun hasSubstep(item: RakuSdkStoreEntry): Boolean {
        return false
    }

    override fun onChosen(item: RakuSdkStoreEntry, choice: Boolean): PopupStep<*>? {
        project.service<RakuProjectSdkService>().setProjectSdkPath(item.path)
        return null
    }
}