package org.raku.comma.services

import ai.grazie.utils.tryRunWithException
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
import org.raku.comma.psi.RakuFile
import org.raku.comma.services.moduleDetails.DependencyDetails
import org.raku.comma.services.moduleDetails.ModuleListFetcher
import org.raku.comma.services.moduleDetails.ProjectModelSync
import org.raku.comma.services.project.RakuProjectDetailsService
import org.raku.comma.utils.CommaProjectUtil

@Service(Service.Level.PROJECT)
@State(name = "Raku.Project.Dependencies", storages = [Storage(value = RakuServiceConstants.RAKU_DEPENDENCY_DETAILS_FILE)])
class RakuModuleDetailsService(
    private val project: Project,
    private val runScope: CoroutineScope
) : PersistentStateComponent<ModuleDetailsState> {

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

    private suspend fun doInitialize() {
        return runScope.launch {
            if (isNotRefreshing) {
                isRefreshing = true
                runScope.future {
                    moduleListFetcher.fillState(moduleDetailsState)
                    dependencyDetails.fillState(moduleDetailsState)
                }.join()

                runScope.future {
                    dependencyDetails.dependenciesToRakuFiles(moduleDetailsState).join()
                    modelSync.syncExternalLibraries(moduleDetailsState.currentDependenciesDeep.toSet())
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
                    isInitialized = true
                    projectService.moduleServiceDidStartup = true
                }.join()
            }
        }
    }

    override fun getState(): ModuleDetailsState {
        return moduleDetailsState
    }

    override fun loadState(newState: ModuleDetailsState) {
        moduleDetailsState = newState
    }

    fun moduleByProvide(provide: String): String? {
        return moduleDetailsState.ecoProvideToModule[provide]
    }

    fun deepDependenciesOf(modules: Collection<String>): Set<String> {
        return moduleListFetcher.dependenciesDeep(modules.toSet())
    }

    fun provideToPsiFile(provide: String): PsiFile? {
        return dependencyDetails.provideToRakuFile(provide)
    }

    fun provideToRakuFile(provide: String): RakuFile? {
        return dependencyDetails.provideToRakuFile(provide) as? RakuFile
    }

    fun allProvides(): Set<String> {
        return moduleListFetcher.moduleList.filter { it.provides.isNotEmpty() }
                                           .flatMap { it.provides.values }
                                           .toSet()
    }

    fun moduleToRakuFiles(module: String): List<RakuFile> {
        val provides = moduleDetailsState.ecoModuleToProvides[module] ?: return listOf()
        return provides.mapNotNull { provideToRakuFile(it) }
    }

    fun isExtendedDependency(module: String): Boolean {
        return moduleDetailsState.currentDependenciesDeep.contains(module)
    }

    fun dependencyInMeta(provide: String): Boolean {
        val module = moduleByProvide(provide) ?: return false
        val direct = CommaProjectUtil.projectDependencies(project)
        if (moduleDetailsState.directDependencies.toSet() != direct) {
            moduleDetailsState.directDependencies = direct.toMutableList()
        }

        return direct.contains(module)
    }
}

class ModuleDetailsState : BaseState() {
    var ecoProvideToPath by map<String, String>()
    var ecoProvideToModule by map<String, String>()
    var ecoModuleToProvides by map<String, List<String>>()
    var moduleNames by list<String>()

    var dependencyToPath by map<String, String>()
    var directDependencies by list<String>()
    var secondaryDependencies by list<String>()
    var currentDependenciesDeep by list<String>()
    var installedDependenciesDeep by list<String>()
}