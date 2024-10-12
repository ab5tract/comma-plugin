package org.raku.comma.project.activity

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.components.service
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.platform.util.progress.reportProgress
import org.raku.comma.event.ModuleMetaChangeListener
import org.raku.comma.services.project.RakuMetaDataComponent
import org.raku.comma.services.project.RakuDependencyDetailsService
import org.raku.comma.services.project.RakuModuleListFetcher
import java.util.concurrent.CompletableFuture

class RakuServiceStarter : ProjectActivity {
    override suspend fun execute(project: Project) {

        val dependencyService = project.service<RakuDependencyDetailsService>()
        dependencyService.state

        // Initialize metadata listeners
        project.service<RakuMetaDataComponent>()
        val modules = ModuleManager.getInstance(project).modules
        for (module in modules) {
            module.service<ModuleMetaChangeListener>()
        }

        // Load all the RakuFiles for our extended dependencies
        withBackgroundProgress(project, "Preloading dependency files") {
            reportProgress { reporter ->
                 reporter.sizedStep(100, "Loading dependencies ") {
                     dependencyService.dependenciesToRakuFiles()
                }
            }.await()
            dependencyService.preloadFinished.complete(true)
        }

        ProjectView.getInstance(project).refresh()

        // Trigger the module list fetcher, if necessary
        val moduleService = project.service<RakuModuleListFetcher>()
        moduleService.state
    }
}
