package org.raku.comma.project.activity

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.profile.codeInspection.InspectionProfileManager
import org.raku.comma.utils.CommaProjectUtil

class ProjectIsRakudoCoreDetector : ProjectActivity {
    private val groupId = "raku.messages"
    private val inspectionsToDisable = listOf("RakuCallArity",
                                              "RakuBuiltinSubmethod",
                                              "RakuUndeclaredOrDeprecatedRoutine",
                                              "RakuRakudoImplementationDetail",
                                              "RakuUndeclaredAttribute")

    override suspend fun execute(project: Project) {
        if (CommaProjectUtil.isRakudoCoreProject(project) && checkForInspections(project)) {

            val text = "Disable problematic inspections"
            val doDisable = {
                disableAnnoyingInspections(project)
                Notifications.Bus.notify(
                    Notification(
                        groupId,
                        "Inspections successfully disabled",
                        """
                        <html>
                        <p><b>Happy hacking!</b></p>
                        </html>
                        """,
                        NotificationType.INFORMATION
                    ),
                    project
                )
            }
            val disableInspectionsAction = NotificationAction.createSimpleExpiring(text, doDisable)

            Notifications.Bus.notify(
                Notification(
                    groupId,
                    "Rakudo source code detected in project",
                    """
                    <html>
                    <p>Some code inspections are either irrelevant or problematic while editing Rakudo source code.</p>
                    </html>
                    """,
                    NotificationType.INFORMATION
                ).addAction(disableInspectionsAction),
                project
            )
        }
    }

    // We only present this option if *all* inspections are currently enabled. The reasoning
    // is that the user may have chosen some custom combination of inspections of their own
    // accord. If that is the case, we don't want to bother them every time they open the
    // project.
    private fun checkForInspections(project: Project) : Boolean {
        val profile = InspectionProfileManager.getInstance(project).currentProfile
        return inspectionsToDisable.stream()
                                   .allMatch({ inspection -> profile.getToolDefaultState(inspection, project).isEnabled })
    }

    private fun disableAnnoyingInspections(project: Project) {
        InspectionProfileManager.getInstance(project)
                                 .currentProfile.modifyProfile( { model -> run {
                                    model.disableToolByDefault(inspectionsToDisable, project)
                                    model.commit()
                                }})
    }
}