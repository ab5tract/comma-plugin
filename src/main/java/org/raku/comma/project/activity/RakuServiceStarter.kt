package org.raku.comma.project.activity

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.platform.util.progress.reportProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.raku.comma.services.application.RakuEcosystem
import org.raku.comma.services.project.RakuMetaDataComponent
import org.raku.comma.services.project.RakuDependencyService

class RakuServiceStarter : ProjectActivity {
    override suspend fun execute(project: Project) {

        val ecosystemDetails = withContext(Dispatchers.IO) {
            withBackgroundProgress(project, "Loading ecosystem details...") {
                reportProgress { progress ->
                    progress.indeterminateStep {
                        service<RakuEcosystem>().initialize().get()
                    }
                }
            }
        }

        // Initialize metadata listeners
        val metaService = project.service<RakuMetaDataComponent>()
        val metaLoaded = withContext(Dispatchers.IO) {
            metaService.metaLoaded?.get() == true
        }
        if (!metaLoaded) return

        project.service<RakuDependencyService>().initialize()
    }
}
