package org.raku.comma.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.raku.comma.profiler.ProfileResultsChooserDialog

class LoadProfileResultsAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        ProfileResultsChooserDialog(event.project).show()
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabled = event.project != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
