package org.raku.utils

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import org.raku.event.ModuleMetaChangeListener
import org.raku.metadata.RakuMetaDataComponent

class RakuModuleListStarter : ProjectActivity {
    override suspend fun execute(project: Project) {
        // Create service if not yet
        project.getService(RakuModuleListFetcher::class.java)
        RakuModuleListFetcher.refreshModules(project)
        // Initialize metadata listeners
        val modules = ModuleManager.getInstance(project).modules
        for (module in modules) {
            module.getService(RakuMetaDataComponent::class.java)
            module.getService(ModuleMetaChangeListener::class.java)
        }
    }
}
