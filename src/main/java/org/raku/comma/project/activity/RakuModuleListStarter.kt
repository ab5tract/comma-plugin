package org.raku.comma.project.activity

import com.intellij.openapi.components.service
import com.intellij.openapi.components.services
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.raku.comma.event.ModuleMetaChangeListener
import org.raku.comma.metadata.RakuMetaDataComponent
import org.raku.comma.services.project.RakuDependencyDetailsService
import org.raku.comma.services.project.RakuModuleListFetcher

class RakuModuleListStarter : ProjectActivity {
    override suspend fun execute(project: Project) {
        // Trigger the module list fetcher, if necessary
        project.service<RakuModuleListFetcher>().state
        project.service<RakuDependencyDetailsService>().state

        // Initialize metadata listeners
        project.service<RakuMetaDataComponent>()
        val modules = ModuleManager.getInstance(project).modules
        for (module in modules) {
            module.service<ModuleMetaChangeListener>()
        }
    }
}
