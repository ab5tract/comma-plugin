package org.raku.comma.sdk.chooser

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup
import kotlinx.coroutines.CoroutineScope

class RakuSdkStatusBarWidget(
    project: Project,
    coroutineScope: CoroutineScope,
    private var selectedSdkPath: String? = null
) : EditorBasedStatusBarPopup(project, false, coroutineScope) {
    private val ID = "RakuSdkStatusBarWidget"

    override fun ID(): String { return ID }

    override fun createInstance(project: Project): StatusBarWidget {
        TODO("Not yet implemented")
    }

    override fun createPopup(context: DataContext): ListPopup? {
        TODO("Not yet implemented")
    }

    override fun getWidgetState(file: VirtualFile?): WidgetState {
        TODO("Not yet implemented")
    }
}