package org.raku.comma.services.support.moduleDetails

import com.intellij.execution.ExecutionException
import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.platform.util.progress.reportProgress
import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.raku.comma.psi.RakuElementFactory
import org.raku.comma.psi.RakuFile
import org.raku.comma.sdk.RakuSdkUtil
import org.raku.comma.services.project.ModuleDetailsState
import org.raku.comma.services.project.RakuProjectSdkService
import org.raku.comma.utils.CommaProjectUtil
import org.raku.comma.utils.RakuCommandLine
import org.raku.comma.utils.RakuUtils
import java.io.File
import java.util.concurrent.CompletableFuture

class DependencyDetails(private val project: Project, private val runScope: CoroutineScope) {

    // This should *only* be called by the RakuDependencyService.doInitialize()
    fun dependenciesToRakuFiles(state: ModuleDetailsState): Deferred<ModuleDetailsState> {
        return runScope.async {
            fillProvidesToRakuFiles(state)
        }
    }

    private suspend fun fillProvidesToRakuFiles(state: ModuleDetailsState): ModuleDetailsState {
        val deep = state.provideDependenciesDeep
        val provides = (deep - state.provideToRakuFile.keys).toMutableList()

        val dependencyToPath = mutableMapOf<String, String>()
        val deferred: List<Deferred<RakuFileWrapper>> =
            pathOfProvideReference(state, provides).mapNotNull { (provide, files) ->
                val path = files.first()
                dependencyToPath.putIfAbsent(provide, path)
                return@mapNotNull createModulePsiFile(project, provide, path)
            }

        val finalToPath =   if (dependencyToPath.isNotEmpty())
                                state.dependencyToPath + dependencyToPath
                            else state.dependencyToPath

        val newToRakuFileLookup = mutableMapOf<String, RakuFile>()
        newToRakuFileLookup.putAll(state.provideToRakuFile)
        deferred.awaitAll().forEach { wrapper ->
            if (wrapper.rakuFile == null) return@forEach

            val rakuFile = wrapper.rakuFile
            rakuFile.moduleName = wrapper.moduleName
            rakuFile.originalPath = wrapper.path
            rakuFile.dependencyFile = true
            newToRakuFileLookup[rakuFile.moduleName] = rakuFile
        }

        // TODO: Prompt to install missing dependencies
        return state.copy(dependencyToPath  = finalToPath,
                          provideToRakuFile = newToRakuFileLookup)
    }

    @Synchronized
    fun fillState(state: ModuleDetailsState): ModuleDetailsState {
        // TODO: Address potential duplicates in the dependency list and multiple paths in the provides output
        val direct = CommaProjectUtil.projectDependencies(project).toSet()
        val deep = dependenciesDeep(state, direct)

        return state.copy(currentDependenciesDeep = deep,
                          provideDependenciesDeep = deep.flatMap { state.ecoModuleToProvides[it] ?: listOf() }.toSet(),
                          directDependencies      = direct,
                          secondaryDependencies   = (deep - direct))
    }

    private fun pathOfProvideReference(
        state: ModuleDetailsState,
        references: MutableList<String>
    ): Map<String, List<String>> {
        val provideMapFuture: CompletableFuture<Map<String, List<String>>> = CompletableFuture()

        if (references.isEmpty()) {
            provideMapFuture.complete(emptyMap())
            return emptyMap()
        }

        val sdkHome = project.service<RakuProjectSdkService>().sdkPath

        val provideMap: MutableMap<String, List<String>> = mutableMapOf()
        references.forEach {
            if (state.dependencyToPath[it] != null) {
                provideMap[it] = listOf(state.dependencyToPath[it]!!)
            }
        }
        provideMap.keys.forEach { references.remove(it) }

        return runScope.future {
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
                return@future provideMap
            } catch (e: ExecutionException) {
                RakuSdkUtil.reactToSdkIssue(project, "Cannot use current Raku SDK")
                return@future mapOf()
            }
        }.join()
    }

    private fun dependenciesDeep(state: ModuleDetailsState, modules: Set<String>): Set<String> {
        val find = { moduleSet: Set<String> ->
            moduleSet.mapNotNull { state.ecosystemRepository[it] }
                     .flatMap   { listOf(listOf(it.name!!), it.depends.map(RakuUtils::stripAuthVerApi)).flatten() }
                     .toMutableSet()
        }

        val seen = find(modules)
        var lookup: Set<String> = seen - modules

        while (lookup.isNotEmpty()) {
            val thisRound = lookup // just to keep it a bit more readable
            seen.addAll(thisRound)
            lookup = find(thisRound) - seen
        }

        return seen.toSet()
    }

   private fun createModulePsiFile(project: Project, name: String, path: String): Deferred<RakuFileWrapper> {
        return runScope.async {
            withBackgroundProgress(project, "Loading modules...") {
                reportProgress { reporter ->
                    reporter.itemStep("Loading module $name") {
                        return@itemStep smartReadAction(project) {
                            val rakuFile = RakuElementFactory.createModulePsiFile(project,
                                                                                  File(path).readText(),
                                                                                  name,
                                                                                  path) as? RakuFile
                            RakuFileWrapper(rakuFile = rakuFile, moduleName = name, path = path)
                        }
                    }
                }
            }
        }
    }
}

data class RakuFileWrapper(val rakuFile: RakuFile?, val moduleName: String, val path: String)

@Serializable
data class PathLookupResult(val notInstalled: List<String>, val pathLookup: Map<String, List<String>>) {
    fun pathOf(module: String): String? {
        return pathLookup[module]?.first()
    }
}