package org.raku.comma.services.project

import com.intellij.execution.ExecutionException
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.ui.EditorNotificationPanel
import kotlinx.coroutines.Job
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import org.raku.comma.pm.RakuPackageManager
import org.raku.comma.pm.RakuPackageManagerManager
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.event.HyperlinkEvent

@Service(Service.Level.PROJECT)
class RakuModuleInstallPrompt(private val project: Project, val runScope: CoroutineScope) {

    fun install(module: String): CompletableFuture<Boolean> {
        val pm = project.service<RakuPackageManagerManager>().currentPM ?: return CompletableFuture.completedFuture(false)

        val completable = CompletableFuture<Boolean>()
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                pm.addInstall(module)
                pm.install()
                completable.complete(true)
            } catch (e: ExecutionException) {
                LOG.warn("Could not install a distribution: " + e.message)
                completable.completeExceptionally(e)
            }
        }
        return completable
    }

    // Can only be usefully called *after* the dependency information is loaded.
    // However, we don't check for this directly because we call it as the last
    // stage of initializing the dependency service.
    fun installMissing() {
        val pmManager = project.service<RakuPackageManagerManager>()
        val currentPM = pmManager.currentPM ?: return

        val metadata = project.service<RakuMetaDataComponent>()
        val dependencies = metadata.allDependencies

        val dependencyService = project.service<RakuDependencyService>()
        val loadedDependencies =  dependencyService.moduleDetails.provideToRakuFile.keys
        // There can be some distributions that don't install a module of their name
        val unavailableDeps = dependencies.filterNot { module ->
            if (loadedDependencies.contains(module)) return@filterNot true
            val provides = dependencyService.moduleDetails.ecoModuleToProvides[module] ?: listOf()
            return@filterNot loadedDependencies.containsAll(provides)
        }

        if (unavailableDeps.isEmpty()) return
        // Report if there are modules to install
        val editors = FileEditorManager.getInstance(project).selectedEditors
        ApplicationManager.getApplication().invokeAndWait {
            for (fileEditor in editors) {
                val notification = getPanel(currentPM, unavailableDeps)
                FileEditorManager.getInstance(project).addTopComponent(fileEditor!!, notification)
                notification.setDismissCallback {
                    FileEditorManager.getInstance(project).removeTopComponent(fileEditor, notification)
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
            RakuModuleInstallPrompt::class.java
        )
    }

    private fun getPanel(pm: RakuPackageManager, unavailableDeps: List<String>): DismissableNotificationPanel {
        val panel = DismissableNotificationPanel()
        panel.text =
            "Some Raku dependencies for this project are not installed (" + getListText(
                unavailableDeps
            ) + ")."
        val installButtonText = "Install with " + pm.kind.name.lowercase(Locale.getDefault())
        val startedProcessing = AtomicBoolean(false)
        panel.createActionLabel(installButtonText, object : EditorNotificationPanel.ActionHandler {
            override fun handlePanelActionClick(panel: EditorNotificationPanel, event: HyperlinkEvent) {
                if (startedProcessing.get()) return
                startedProcessing.set(true)

                panel.text += " Installing..."
                ApplicationManager.getApplication().executeOnPooledThread {
                    unavailableDeps.forEach { dep ->
                        pm.addInstall(dep)
                    }
                    try {
                        pm.installEach()
                    } catch (e: ExecutionException) {
                        LOG.warn("Could not install a distribution: " + e.message)
                    }
                    panel.parent.remove(panel)
                }
                startedProcessing.set(false)
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