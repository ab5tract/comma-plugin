package org.raku.comma.utils

import com.intellij.execution.ExecutionException
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.platform.util.progress.reportProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.raku.comma.metadata.data.ExternalMetaFile
import org.raku.comma.sdk.RakuSdkUtil
import org.raku.comma.services.application.RakuEcosystem
import org.raku.comma.services.project.*
import java.io.File
import java.nio.file.Path
import java.util.concurrent.CompletableFuture

/**
 * A simpler, Comma-exclusive re-imagination of NewProjectUtil.
 */
object CommaProjectUtil {

    @JvmStatic
    fun isRakudoCoreProject(project: Project): Boolean {
        return project.service<RakuProjectDetailsService>().isProjectRakudoCore()
    }

    // Technically this should also be available at the Facet level so that different project-modules can have their
    // own META6.json. Fix this later!
    @JvmStatic
    fun projectDependencies(project: Project): List<String> {
        return if (projectHasMetaFile(project)) metaFile(project).depends.map { RakuUtils.stripAuthVerApi(it) } else listOf()
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    fun metaFile(project: Project): ExternalMetaFile {
        val meta6path = "%s%sMETA6.json".format(project.basePath, File.separator)
        check(Path.of(meta6path).toFile().exists()) { "There is no META6.json file in project path " + project.basePath }
        return json.decodeFromString(File(meta6path).readText())
    }

    fun projectHasMetaFile(project: Project): Boolean {
        val meta6path = "%s%sMETA6.json".format(project.basePath, File.separator)
        return Path.of(meta6path).toFile().exists()
    }

    suspend fun refreshProjectState(project: Project) {
        val sdkService = project.service<RakuProjectSdkService>()
        val maybeInstallZef =   if (sdkService.zef == null)
                                    project.service<RakuModuleInstallPrompt>().installZefItself()
                                else CompletableFuture.completedFuture(0)

        withContext(Dispatchers.IO) {
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

        if (withContext(Dispatchers.IO) { maybeInstallZef.get() } == 0) {
            project.service<RakuProjectDetailsService>().moduleServiceDidStartup = false
            project.service<RakuDependencyService>().initialize().join()
            project.service<RakuModuleInstallPrompt>().installMissing()
        }
    }
}
