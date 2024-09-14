package org.raku.comma.utils

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.raku.comma.RakuIcons
import org.raku.comma.actions.UpdateExtensionsAction
import org.raku.comma.actions.UpdateExtensionsAction.RakuChooseExtensionsToUpdateDialog

class RakuLegacyExtensionsDetector : ProjectActivity {
    override suspend fun execute(project: Project) {
        val modules = ModuleManager.getInstance(project).modules
        val filesToUpdate = UpdateExtensionsAction.collectFilesWithLegacyNames(modules)
        if (filesToUpdate.isNotEmpty()) {
            val notification = Notification(
                "raku.misc", "Obsolete Raku extensions are detected",
                "Obsolete file extensions are detected: " + filesToUpdate.keys.joinToString(", "),
                NotificationType.WARNING
            )
            notification.setIcon(RakuIcons.CAMELIA)
            notification.addAction(object : AnAction("Run Comma Legacy File Rename Tool") {
                override fun actionPerformed(e: AnActionEvent) {
                    notification.expire()
                    RakuChooseExtensionsToUpdateDialog(project, filesToUpdate).show()
                }
            })
            Notifications.Bus.notify(notification)
        }
    }
}
