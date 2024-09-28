package org.raku.comma.project.activity

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.raku.comma.event.ModuleMetaChangeListener
import org.raku.comma.metadata.RakuMetaDataComponent
import org.raku.comma.services.RakuDependencyDetailsService
import org.raku.comma.services.RakuModuleListFetcher

class RakuModuleListStarter : ProjectActivity {
    override suspend fun execute(project: Project) {
        // Trigger the module list fetcher, if necessary
        val moduleService = project.getService(RakuModuleListFetcher::class.java)
        moduleService.state

        val dependencyService = project.getService(RakuDependencyDetailsService::class.java)
        dependencyService.state

        // Initialize metadata listeners
        val modules = ModuleManager.getInstance(project).modules
        for (module in modules) {
            module.getService(RakuMetaDataComponent::class.java)
            module.getService(ModuleMetaChangeListener::class.java)
        }
    }
}
