package org.raku.comma.project.activity

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.raku.comma.utils.CommaProjectUtil

class ProjectIsRakudoCoreAnnouncer : ProjectActivity {
    override suspend fun execute(project: Project) {
        if (CommaProjectUtil.isRakudoCoreProject(project)) {
            Notifications.Bus.notify(
                Notification(
                    "raku.messages",
                    "Rakudo source code detected in project",
                    "<html><p>Special settings for core hacking enabled.</p><p><b>Happy hacking!</b></p></html>",
                    NotificationType.INFORMATION
                ),
                project
            )
        }
    }
}