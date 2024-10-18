package org.raku.comma.services

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.platform.util.progress.reportProgress
import java.util.concurrent.atomic.AtomicBoolean
import com.intellij.psi.PsiFile
import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import org.raku.comma.metadata.data.ExternalMetaFile
import org.raku.comma.psi.RakuFile
import org.raku.comma.services.moduleDetails.DependencyDetails
import org.raku.comma.services.moduleDetails.ModuleListFetcher
import org.raku.comma.services.moduleDetails.ProjectModelSync
import org.raku.comma.services.project.RakuProjectDetailsService
import org.raku.comma.utils.CommaProjectUtil

@Service(Service.Level.PROJECT)
class RakuModuleDetailsService(private val project: Project, private val runScope: CoroutineScope) {

    private val initializationStatus = AtomicBoolean(false)
    var isInitialized: Boolean
        get() = initializationStatus.get()
        set(value) { initializationStatus.set(value) }
    val isNotInitialized: Boolean get() = !isInitialized

    private val refreshStatus = AtomicBoolean(false)
    var isRefreshing: Boolean
        get() = refreshStatus.get()
        set(value) { refreshStatus.set(value) }
    val isNotRefreshing: Boolean
        get() = !isRefreshing

    private val dependencyDetails = DependencyDetails(project, runScope)
    private val moduleListFetcher = ModuleListFetcher(project, runScope)
    private val modelSync = ProjectModelSync(project, runScope)

    private var moduleDetailsState = ModuleDetailsState()
    val moduleDetails: ModuleDetailsState
        get() = moduleDetailsState

    private suspend fun doInitialize() {
        return runScope.launch {
            if (isNotRefreshing) {
                var newState = ModuleDetailsState()
                isRefreshing = true
                runScope.future {
                    newState = moduleListFetcher.fillState(newState)
                    newState = dependencyDetails.fillState(newState)
                }.join()

                runScope.future {
                    newState = dependencyDetails.dependenciesToRakuFiles(newState).await()
                    modelSync.syncExternalLibraries(newState.currentDependenciesDeep)
                    moduleDetailsState = newState
                    isRefreshing = false
                    runScope.future {
                        withContext(Dispatchers.EDT) {
                            ProjectView.getInstance(project).refresh()
                        }
                    }.join()
                }.join()

            }
        }.join()
    }


    @Synchronized
    fun initialize(): Job {
        return runScope.launch {
            val projectService = project.service<RakuProjectDetailsService>()
            if (projectService.noModuleServiceDidNotStartup) {
                isInitialized = false
                runScope.future {
                    withBackgroundProgress(project, "Refreshing ecosystem and dependency information") {
                        reportProgress {  reporter ->
                            reporter.sizedStep(23, "Refreshing ecosystem and dependency information") {
                                doInitialize()
                            }
                        }
                    }
                    projectService.moduleServiceDidStartup = true
                    isInitialized = true
                }.join()
            }
        }
    }

    fun moduleByProvide(provide: String): String? {
        return moduleDetailsState.ecoProvideToModule[provide]
    }

    fun provideToPsiFile(provide: String): PsiFile? {
        return provideToRakuFile(provide)
    }

    fun provideToRakuFile(provide: String): RakuFile? {
        return moduleDetailsState.provideToRakuFileLookup[provide]
    }

    fun allProvides(): Set<String> {
        return moduleDetailsState.metaFiles.filter { it.provides.isNotEmpty() }
                                           .flatMap { it.provides.values }
                                           .toSet()
    }

    fun moduleToRakuFiles(module: String): List<RakuFile> {
        val provides = moduleDetailsState.ecoModuleToProvides[module] ?: return listOf()
        return provides.mapNotNull { provideToRakuFile(it) }
    }

    fun dependencyInMeta(provide: String): Boolean {
        // First attempt resolution against loaded direct dependencies
        val currentDirect = moduleDetailsState.directDependencies.toSet()
        if (currentDirect.contains(provide)) return true
        // Look up the module via provides and try resolving that way
        val module = moduleByProvide(provide) ?: return false

        if (currentDirect.contains(module)) return true

        // Otherwise check whether the metaFile contents are different
        val metaDirect = CommaProjectUtil.projectDependencies(project).toSet()
        if (moduleDetailsState.directDependencies.toSet() != metaDirect) {
            moduleDetailsState = moduleDetailsState.copy(directDependencies = metaDirect)
            return metaDirect.contains(module)
        }

        // None of the above resolved, so the file isn't included
        return false
     }
}

data class ModuleDetailsState(
    val ecoProvideToPath: Map<String, String> = mapOf(),
    val ecoProvideToModule: Map<String, String> = mapOf(),
    val ecoModuleToProvides: Map<String, List<String>> = mapOf(),
    val dependenciesToRakuFiles: Map<String, RakuFile> = mapOf(),

    val directDependencies: Set<String> = setOf(),
    val secondaryDependencies: Set<String> = setOf(),
    val currentDependenciesDeep: Set<String> = setOf(),
    val installedDependenciesDeep: Set<String> = setOf(),

    val dependencyToPath: Map<String, String> = mapOf(),
    val provideToRakuFileLookup: Map<String, RakuFile> = mapOf(),

    val ecosystemRepository: Map<String, ExternalMetaFile> = mapOf(),
    val moduleNames: List<String> = listOf(),
    val metaFiles: List<ExternalMetaFile> = listOf(),
)