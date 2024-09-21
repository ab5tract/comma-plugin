package org.raku.comma.services

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.raku.comma.utils.CommaProjectUtil

class IsRakudoCoreAnnouncer : ProjectActivity {
    override suspend fun execute(project: Project) {
        if (CommaProjectUtil.isProjectRakudo(project)) {
            Notifications.Bus.notify(
                Notification(
                    "raku.messages",
                    "Project contains rakudo.git.\nuSpecial settings for core hacking enabled.",
                    NotificationType.INFORMATION
                ),
                project
            )
        }
    }
}