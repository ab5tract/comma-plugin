package org.raku.comma.repl

import com.intellij.execution.ExecutionException
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import org.raku.comma.RakuIcons
import org.raku.comma.services.project.RakuProjectSdkService

open class RakuLaunchReplAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        startRepl(event, null)
    }

    protected fun startRepl(event: AnActionEvent, useModule: String?) {
        if (getSdkHome(event) == null || event.project == null) {
            return
        }
        val project = event.project
        val title = if (useModule != null) "Raku REPL (use %s)".format(useModule) else "Raku REPL"
        val console = RakuReplConsole(project!!, title)
        try {
            console.initAndRun()
            if (useModule != null) {
                ApplicationManager.getApplication()
                    .invokeAndWait { console.executeStatement("use $useModule;") }
            }
        } catch (ex: ExecutionException) {
            var notification = Notification(
                "raku.repl.errors", "Cannot run REPL",
                "Could not start Raku REPL", NotificationType.ERROR
            )
            notification.setIcon(RakuIcons.CAMELIA)
            notification = notification.addAction(object : AnAction("Show Exception Details to Report") {
                override fun actionPerformed(e: AnActionEvent) {
                    Logger.getInstance(RakuLaunchReplAction::class.java).error(ex)
                }
            })
            Notifications.Bus.notify(notification, project)
        }
    }

    override fun update(event: AnActionEvent) {
        val available = getSdkHome(event) != null
        event.presentation.isEnabled = available
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    companion object {
        @JvmStatic
        protected fun getSdkHome(event: AnActionEvent): String? {
            val project = event.project ?: return null
            return project.getService(RakuProjectSdkService::class.java).sdkPath
        }
    }
}
