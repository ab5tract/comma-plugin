package org.raku.comma.project.activity

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.raku.comma.services.RakuModuleDetailsService
import org.raku.comma.services.project.RakuMetaDataComponent

class RakuServiceStarter : ProjectActivity {
    override suspend fun execute(project: Project) {

        // Initialize metadata listeners
        val metaService = project.service<RakuMetaDataComponent>()
        val metaLoaded = withContext(Dispatchers.IO) {
            metaService.metaLoaded?.get() == true
        }
        if (!metaLoaded) return

        project.service<RakuModuleDetailsService>().initialize()
    }
}
