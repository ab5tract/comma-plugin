package org.raku.comma.project.activity

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

        // Trigger the module list fetcher, if necessary
        project.service<RakuModuleListFetcher>().state
        val dependencyService = project.service<RakuDependencyDetailsService>()

        // Initialize metadata listeners
        project.service<RakuMetaDataComponent>()
        val modules = ModuleManager.getInstance(project).modules
        for (module in modules) {
            module.service<ModuleMetaChangeListener>()
        }

        // Load all the RakuFiles for our extended dependencies
        withBackgroundProgress(project, "Preloading dependency files") {
            val fileFutures = reportProgress { reporter ->
                 dependencyService.loadedDependencies.map { dependency ->
                     reporter.sizedStep(1, "Loading dependency $dependency") {
                         dependencyService.moduleToRakuFiles(dependency)
                     }
                }
            }
            CompletableFuture.allOf(*fileFutures.toTypedArray()).join()
        }
    }
}
