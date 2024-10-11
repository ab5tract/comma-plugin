package org.raku.comma.services.project

import com.intellij.execution.ExecutionException
import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.components.*
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.platform.util.progress.ProgressReporter
import com.intellij.platform.util.progress.reportProgress
import com.intellij.psi.PsiFile
import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.raku.comma.psi.RakuElementFactory
import org.raku.comma.psi.RakuFile
import org.raku.comma.sdk.RakuSdkUtil
import org.raku.comma.services.RakuServiceConstants
import org.raku.comma.utils.CommaProjectUtil
import org.raku.comma.utils.RakuUtils
import org.raku.comma.utils.RakuCommandLine
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

@Service(Service.Level.PROJECT)
@State(name = "Raku.Dependency.Details", storages = [Storage(value = RakuServiceConstants.RAKU_DEPENDENCY_DETAILS_FILE)])
class RakuDependencyDetailsService(
    private val project: Project,
    private val runScope: CoroutineScope
) : PersistentStateComponent<DependencyDetailsState>, DumbAware {
    var dependencyState = DependencyDetailsState()

    val isReady: Boolean
        get() = checkIsReady()
    val isNotReady: Boolean
        get() = !isReady

    var isGettingReady = false
    val isNotGettingReady: Boolean
        get() = !isGettingReady

    val provideToRakuFileLookup: MutableMap<String, RakuFile> = ConcurrentHashMap()

    val loadedDependencies: Set<String>
        get() = setOf(*state.directDependencies.toTypedArray(), *state.secondaryDependencies.toTypedArray())

    private fun checkIsReady(): Boolean {
        val metaDeps = CommaProjectUtil.projectDependencies(project).toSet()
        val direct = dependencyState.directDependencies.toSet()
        return (metaDeps - direct).isEmpty() && dependencyState.secondaryDependencies.isNotEmpty()
    }

    fun provideToRakuFile(provide: String): PsiFile? {
        if (provideToRakuFileLookup.containsKey(provide)) return provideToRakuFileLookup[provide]

        val path = dependencyState.provideToPath[provide] ?: return null

        // TODO: How can we do this in a smartReadAction without causing the function to become a suspend?
        val rakuFile = RakuElementFactory.createModulePsiFile(project, File(path).readText(), provide, path) as? RakuFile
                            ?: return null

        rakuFile.moduleName = provide
        provideToRakuFileLookup[provide] = rakuFile
        return rakuFile
    }

    fun moduleToRakuFiles(module: String): CompletableFuture<List<RakuFile>> {
        val provides = project.service<RakuModuleListFetcher>().getProvidesListByModule(module)
        addLoadedDependencies(listOf(module))
        return runScope.future {
            withBackgroundProgress(project, "Locating and loading code dependencies") {
                reportProgress { report ->
                    providesToRakuFiles(provides, report)
                }
            }
        }
    }

    fun modulesToRakuFiles(modules: List<String>): List<RakuFile> {
        val provides = project.service<RakuModuleListFetcher>().getProvidesListByModules(modules)
        addLoadedDependencies(modules)

        var files: List<RakuFile>? = null
        runScope.launch {
            withBackgroundProgress(project, "Locating and loading code dependencies") {
                reportProgress { report ->
                    files = providesToRakuFiles(provides, report)
                }
            }
        }

        return files ?: emptyList()
    }

    private fun addLoadedDependencies(dependencies: List<String>) {
        val metaDepends = CommaProjectUtil.projectDependencies(project).toSet()

        val direct = dependencyState.directDependencies.toMutableSet()
        val secondary = dependencyState.secondaryDependencies.toMutableSet()

        direct.addAll(dependencies.filter { metaDepends.contains(it) })
        secondary.addAll(dependencies.filter { !direct.contains(it) })

        dependencyState.directDependencies = direct.toMutableList()
        dependencyState.secondaryDependencies = secondary.toMutableList()
    }

    private suspend fun providesToRakuFiles(provides: List<String>, reporter: ProgressReporter): List<RakuFile> {
        return reporter.sizedStep(10, "Locating and loading dependencies...") {
            runScope.async {
                pathOfProvideReference(provides.toMutableList()).map { (provide, files) ->
                    val path = files.first()
                    dependencyState.provideToPath.putIfAbsent(provide, path)

                    val callable = { _: String ->
                        runScope.async {
                            reporter.sizedStep(1, "Loading $provide") {
                                smartReadAction(project) {
                                    RakuElementFactory.createModulePsiFile(project,
                                                                           File(path).readText(),
                                                                           provide,
                                                                           path)
                                }
                            }
                        }
                    }

                    val rakuFile = if (provideToRakuFileLookup.contains(provide))
                                        provideToRakuFileLookup[provide]!!
                                    else
                                        (runScope.async { callable("").await() }.await()) as RakuFile

                    rakuFile.moduleName = provide
                    rakuFile.originalPath = path
                    rakuFile
                }
            }
        }.await()
    }

    private fun fillState() {
        // TODO: Address potential duplicates in the dependency list and multiple paths in the provides output
        val direct = CommaProjectUtil.projectDependencies(project)

        val pathLookup = mutableMapOf<String, String>()
        val moduleService = project.service<RakuModuleListFetcher>()
        val modules: List<String> = moduleService.dependenciesDeep(direct.toSet()).toList()
        val provides = moduleService.getProvidesListByModules(modules)
        pathLookup.putAll(pathOfProvideReference(provides.toMutableList()).entries.map { Pair(it.key, it.value.first()) })

        dependencyState.provideToPath = pathLookup
        dependencyState.directDependencies = direct.toMutableList()
        dependencyState.secondaryDependencies = (modules.toSet() - direct.toSet()).toMutableList()
    }

    private fun pathOfProvideReference(reference: String): List<String>? {
        return pathOfProvideReference(mutableListOf(reference))[reference]
    }

    private fun pathOfProvideReference(references: MutableList<String>): Map<String, List<String>> {
        val sdkHome = project.service<RakuProjectSdkService>().sdkPath

        val provideMapFuture: CompletableFuture<Map<String, List<String>>> = CompletableFuture()

        val provideMap: MutableMap<String, List<String>> = mutableMapOf()
        references.forEach {
            if (dependencyState.provideToPath[it] != null) {
                provideMap[it] = listOf(dependencyState.provideToPath[it]!!)
            }
        }
        provideMap.keys.forEach { references.remove(it) }

        if (references.isNotEmpty()) {
            runScope.launch {
                try {
                    val locateScript = RakuUtils.getResourceAsFile("scripts/absolute-path-of-module.raku")
                        ?: throw ExecutionException("Resource bundle is corrupted: locate script is missing")
                    val pathCollectorScript = RakuCommandLine(sdkHome)
                    pathCollectorScript.addParameter(locateScript.absolutePath)
                    references.forEach { reference -> pathCollectorScript.addParameter(reference) }
                    val output = pathCollectorScript.executeAndRead(null).joinToString("\n")
                    // TODO: Do something with the notInstalled details
                    val result = Json.decodeFromString<PathLookupResult>(output)
                    provideMap.putAll(result.pathLookup)
                    provideMapFuture.complete(provideMap.toMap())
                } catch (e: ExecutionException) {
                    RakuSdkUtil.reactToSdkIssue(project, "Cannot use current Raku SDK")
                    provideMapFuture.complete(provideMap.toMap())
                }
            }
        } else {
            provideMapFuture.complete(provideMap.toMap())
        }

        return provideMapFuture.join()
    }

    fun refresh() {
        if (isNotReady && isNotGettingReady) {
            isGettingReady = true
//            fillState()
            isGettingReady = false
        }
    }

    override fun getState(): DependencyDetailsState {
        if (isNotReady && isNotGettingReady) {
            isGettingReady = true
            // TODO: Figure out the async story...
            //  runBlocking(dispatchScope.coroutineContext, { fillState() })
            fillState()
            isGettingReady = false
        }
        return dependencyState
    }

    override fun loadState(state: DependencyDetailsState) {
        dependencyState = state
    }
}

class DependencyDetailsState : BaseState() {
    var provideToPath by map<String, String>()
    var directDependencies by list<String>()
    var secondaryDependencies by list<String>()
}

@Serializable
data class PathLookupResult(val notInstalled: List<String>, val pathLookup: Map<String, List<String>>) {
    fun pathOf(module: String): String? {
        return pathLookup[module]?.first()
    }
}