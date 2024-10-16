package org.raku.comma.services.moduleDetails

import com.intellij.execution.ExecutionException
import com.intellij.openapi.application.readAndWriteAction
import com.intellij.openapi.application.smartReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.platform.util.progress.reportProgress
import com.intellij.psi.*
import com.intellij.testFramework.LightVirtualFile
import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.raku.comma.RakuLanguage
import org.raku.comma.filetypes.RakuModuleFileType
import org.raku.comma.psi.RakuElementFactory
import org.raku.comma.psi.RakuFile
import org.raku.comma.sdk.RakuSdkUtil
import org.raku.comma.services.ModuleDetailsState
import org.raku.comma.services.project.RakuProjectSdkService
import org.raku.comma.utils.CommaProjectUtil
import org.raku.comma.utils.RakuCommandLine
import org.raku.comma.utils.RakuUtils
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

class DependencyDetails(private val project: Project, private val runScope: CoroutineScope) {

    val provideToRakuFileLookup: MutableMap<String, RakuFile> = ConcurrentHashMap()

    private val creatingRakuFiles = AtomicBoolean(false)
    var isCreatingRakuFiles: Boolean
        get() = creatingRakuFiles.get()
        set(value) = creatingRakuFiles.set(value)
    val isNotCreatingRakuFiles: Boolean get() = !creatingRakuFiles.get()

    fun provideToRakuFile(provide: String): PsiFile? {
        return provideToRakuFileLookup[provide]
    }

    private fun verifyRakuFileLookup(state: ModuleDetailsState): Boolean {
        return state.dependencyToPath.size == provideToRakuFileLookup.size
                && (provideToRakuFileLookup.isNotEmpty() && state.directDependencies.isNotEmpty())
    }

    // This should *only* be called by the RakuServiceStarter
    fun dependenciesToRakuFiles(state: ModuleDetailsState): Job {
        return runScope.launch {
            fillProvidesToRakuFiles(state)
        }
    }

    private fun areRakuFilesFilled(state: ModuleDetailsState): Boolean {
        val deep = state.currentDependenciesDeep
        if (deep.isEmpty() && CommaProjectUtil.projectDependencies(project).isEmpty()) return true

        val currentLookup = provideToRakuFileLookup.keys.toSet()
        return currentLookup.isNotEmpty() && currentLookup == state.installedDependenciesDeep.toSet()
    }

    private suspend fun fillProvidesToRakuFiles(state: ModuleDetailsState) {

        val deep = state.installedDependenciesDeep.ifEmpty { state.currentDependenciesDeep }

        val provides = deep.filter { provideToRakuFileLookup[it] == null }
            .flatMap { state.ecoModuleToProvides[it] ?: emptyList() }

        val deferred: List<Deferred<RakuFileWrapper>> =
            pathOfProvideReference(state, provides.toMutableList()).mapNotNull { (provide, files) ->
                val path = files.first()
                state.dependencyToPath.putIfAbsent(provide, path)

                return@mapNotNull createModulePsiFile(project, provide, path)
            }

        deferred.awaitAll().forEach { wrapper ->
            if (wrapper.rakuFile == null) return@forEach

            val rakuFile = wrapper.rakuFile
            rakuFile.moduleName = wrapper.moduleName
            rakuFile.originalPath = wrapper.path
            provideToRakuFileLookup[rakuFile.moduleName] = rakuFile
        }

        // TODO: Prompt to install missing dependencies
        state.installedDependenciesDeep = provideToRakuFileLookup.keys.toMutableList()
    }

    @Synchronized
    fun fillState(state: ModuleDetailsState) {
        // TODO: Address potential duplicates in the dependency list and multiple paths in the provides output
        val direct = CommaProjectUtil.projectDependencies(project).toSet()
        val deep = state.currentDependenciesDeep.toSet()

        val pathLookup = mutableMapOf<String, String>()
        val alreadyLoaded = state.dependencyToPath.keys.toSet()

//        val stillUnfilled = (deep - alreadyLoaded) + (alreadyLoaded - deep)
        val provideMap =
//            pathOfProvideReference(state, stillUnfilled.toMutableList()).entries.map { Pair(it.key, it.value.first()) }
            pathOfProvideReference(state, deep.toMutableList()).entries.map { Pair(it.key, it.value.first()) }
        pathLookup.putAll(provideMap)

        state.dependencyToPath.putAll(pathLookup)
        state.directDependencies = direct.toMutableList()
        state.secondaryDependencies = (deep - direct).toMutableList()
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