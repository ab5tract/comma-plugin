package org.raku.comma.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.raku.comma.sdk.RakuSdkChooserUI

class SdkSetterAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        RakuSdkChooserUI(project).show()
    }

    override fun update(event: AnActionEvent) {
        if (event.project == null) {
            event.presentation.isEnabledAndVisible = false
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT
}
