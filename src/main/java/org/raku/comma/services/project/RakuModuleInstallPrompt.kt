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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.raku.comma.pm.RakuPackageManager
import org.raku.comma.pm.impl.RakuZefPM
import org.raku.comma.utils.CommaProjectUtil
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.event.HyperlinkEvent

@Service(Service.Level.PROJECT)
class RakuModuleInstallPrompt(private val project: Project, val runScope: CoroutineScope) {

    fun install(module: String): CompletableFuture<Boolean> {
        val pm = project.service<RakuProjectSdkService>().zef ?: return CompletableFuture.completedFuture(false)

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

    fun installZefItself(): CompletableFuture<Int> {
        val completable = CompletableFuture<Int>()
        val pm = RakuZefPM(project)

        val editors = FileEditorManager.getInstance(project).selectedEditors
        ApplicationManager.getApplication().invokeAndWait {
            for (fileEditor in editors) {
                val notification = getPanelZefItselfInstall(pm, completable)
                FileEditorManager.getInstance(project).addTopComponent(fileEditor!!, notification)
                notification.setDismissCallback {
                    if (! completable.isDone) {
                        completable.complete(-1);
                    }
                    FileEditorManager.getInstance(project).removeTopComponent(fileEditor, notification)
                }
            }
        }

        return completable
    }

    // Can only be usefully called *after* the dependency information is loaded.
    // However, we don't check for this directly because we call it as the last
    // stage of initializing the dependency service.
    fun installMissing() {
        val projectDetailsService = project.service<RakuProjectDetailsService>()
        if (projectDetailsService.hasNotifiedMissingDependencies) return
        projectDetailsService.hasNotifiedMissingDependencies = true

        val currentPM = project.service<RakuProjectSdkService>().zef ?: return

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
                val notification = getPanelModuleInstall(currentPM as RakuZefPM, unavailableDeps)
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

    private fun getPanelZefItselfInstall(pm: RakuZefPM, completion: CompletableFuture<Int>): DismissableNotificationPanel {
        val sdkName = project.service<RakuProjectSdkService>().sdkName
        val panelFormat = "No 'zef' found in environment path for SDK version $sdkName"
        val installButtonFormat = "Install zef"
        return getPanel(pm, emptyList(), panelFormat, installButtonFormat, { pm.installPackageManager(completion) })
    }

    private fun getPanelModuleInstall(pm: RakuZefPM, arguments: List<String>): DismissableNotificationPanel {
        val panelText = "Some Raku dependencies for this project are not installed (${arguments.joinToString(", ") })."
        val installButtonText = "Install missing dependencies"
        return getPanel(pm, arguments, panelText, installButtonText, { pm.installEach(); 0 })
    }

    private fun getPanel(
        pm: RakuPackageManager,
        arguments: List<String>,
        panelText: String,
        installButtonText: String,
        installClosure: () -> Int,
    ): DismissableNotificationPanel {
        val panel = DismissableNotificationPanel()
        panel.text = panelText
        val startedProcessing = AtomicBoolean(false)
        panel.createActionLabel(installButtonText, object : EditorNotificationPanel.ActionHandler {
            override fun handlePanelActionClick(panel: EditorNotificationPanel, event: HyperlinkEvent) {
                if (startedProcessing.get()) return
                startedProcessing.set(true)

                panel.text += " Installing..."
                ApplicationManager.getApplication().executeOnPooledThread {
                    arguments.forEach { dep ->
                        pm.addInstall(dep)
                    }
                    try {
                        installClosure.invoke()
                        runScope.launch {
                            withContext(Dispatchers.Default) {
                                CommaProjectUtil.refreshProjectState(project)
                            }
                        }
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
}