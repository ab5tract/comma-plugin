package org.raku.comma.pm

import com.intellij.execution.ExecutionException
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.psi.PsiFile
import com.intellij.ui.EditorNotificationPanel
import org.raku.comma.services.project.RakuMetaDataComponent
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Collectors
import javax.swing.event.HyperlinkEvent

class RakuModuleInstallPromptStarter : ProjectActivity {
    override suspend fun execute(project: Project) {
        val pmManager = project.getService(RakuPackageManagerManager::class.java)
        if (pmManager == null || pmManager.currentPM == null) return
        val currentPM = pmManager.currentPM

        val unavailableDeps: MutableList<String> = ArrayList()

        val metadata = project.getService(RakuMetaDataComponent::class.java)
        val dependencies = metadata.allDependencies
        try {
            val installedDists = currentPM.getInstalledDistributions(project)
                .stream().map { s: String? -> RakuDependencySpec(s) }.collect(Collectors.toSet())
            for (depFromMeta in dependencies) {
                val hasDep = installedDists.stream()
                    .anyMatch { idFromPM: RakuDependencySpec -> idFromPM == RakuDependencySpec(depFromMeta) }
                if (!hasDep) unavailableDeps.add(depFromMeta)
            }
        } catch (e: ExecutionException) {
            LOG.info("Could not query installed modules: " + e.message)
        }

        if (unavailableDeps.isEmpty()) return

        // Report if there are modules to install
        val editors = FileEditorManager.getInstance(project).selectedEditors
        ApplicationManager.getApplication().invokeAndWait {
            for (fileEditor in editors) {
                val notification = getPanel(project, pmManager.currentPM, unavailableDeps)
                FileEditorManager.getInstance(project).addTopComponent(fileEditor!!, notification)
                notification.setDismissCallback {
                    FileEditorManager.getInstance(project).removeTopComponent(
                        fileEditor, notification
                    )
                }
            }
        }
    }

    internal class DismissableNotificationPanel : EditorNotificationPanel() {
        fun setDismissCallback(callback: Runnable) {
            createActionLabel("Dismiss", object : ActionHandler {
                override fun handlePanelActionClick(panel: EditorNotificationPanel, event: HyperlinkEvent) {
                    callback.run()
                }

                override fun handleQuickFixClick(editor: Editor, psiFile: PsiFile) {}
            }, true)
        }
    }

    companion object {
        val LOG = Logger.getInstance(
            RakuModuleInstallPromptStarter::class.java
        )
    }

    private fun getPanel(
        project: Project,
        pm: RakuPackageManager,
        unavailableDeps: List<String>
    ): DismissableNotificationPanel {
        val panel = DismissableNotificationPanel()
        panel.text =
            "Some Raku dependencies for this project are not installed (" + getListText(
                unavailableDeps
            ) + ")."
        val installButtonText = "Install with " + pm.kind.getName()
        val startedProcessing = AtomicBoolean()
        panel.createActionLabel(installButtonText, object : EditorNotificationPanel.ActionHandler {
            override fun handlePanelActionClick(panel: EditorNotificationPanel, event: HyperlinkEvent) {
                if (!startedProcessing.compareAndSet(false, true)) {
                    return
                }
                panel.text = panel.text + " Installing..."
                ApplicationManager.getApplication().executeOnPooledThread {
                    for (dep in unavailableDeps) {
                        try {
                            pm.install(project, dep)
                        } catch (e: ExecutionException) {
                            LOG.warn("Could not install a distribution '" + dep + "': " + e.message)
                        }
                    }
                    panel.parent.remove(panel)
                }
            }

            override fun handleQuickFixClick(editor: Editor, psiFile: PsiFile) {}
        }, true)
        return panel
    }

    private fun getListText(deps: List<String>): String {
        val joiner = StringJoiner(", ")
        var complete = true
        for (dep in deps) {
            joiner.add(dep)
            if (joiner.length() > 80) {
                complete = false
                break
            }
        }
        return joiner.toString() + (if (complete) "" else "...")
    }
}