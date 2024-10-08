package org.raku.comma.services.project

import com.intellij.execution.ExecutionException
import com.intellij.openapi.components.*
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
        get() = dependencyState.loadedDependencies.toSet().equals(CommaProjectUtil.projectDependencies(project).toSet())
    val isNotReady: Boolean
        get() = !isReady

    var isGettingReady = false
    val isNotGettingReady: Boolean
        get() = !isGettingReady

    val provideToRakuFileLookup: MutableMap<String, PsiFile> = ConcurrentHashMap()

    fun provideToRakuFile(provide: String): PsiFile? {
        if (provideToRakuFileLookup.containsKey(provide)) return provideToRakuFileLookup[provide]

        val path = dependencyState.provideToPath[provide] ?: return null
        // TODO: Figure out some way to notify the user in the rare case that this is failing due to an
        // an actual user interaction and not an automated intention sweep or other resolve-y thing
        //      ?: throw RuntimeException("Could not find location of file providing '%s'".format(provide))

        // TODO: Maybe prefill these, if it's still too slow
        val rakuFile = RakuElementFactory.createModulePsiFile(project, File(path).readText(), provide, path)
                            ?: return null

        (rakuFile as RakuFile).moduleName = provide
        provideToRakuFileLookup[provide] = rakuFile
        return rakuFile
    }

    fun moduleToRakuFiles(module: String): List<RakuFile> {
        val provides = project.service<RakuModuleListFetcher>().getProvidesListByModule(module)
        return providesToRakuFiles(provides)
    }

    fun modulesToRakuFiles(modules: List<String>): List<RakuFile> {
        val provides = project.service<RakuModuleListFetcher>().getProvidesListByModules(modules)
        return providesToRakuFiles(provides)
    }

    private fun providesToRakuFiles(provides: List<String>): List<RakuFile> {
        return pathOfProvideReference(provides).map { (provide, files) ->
            val path = files.first()
            val rakuFile = provideToRakuFileLookup.computeIfAbsent(provide,
                                { RakuElementFactory.createModulePsiFile(project,
                                                                         File(path).readText(),
                                                                         provide,
                                                                         path)
                                }) as RakuFile

            rakuFile.moduleName = provide
            rakuFile.originalPath = path
            rakuFile
        }.toList()
    }

    private fun fillState() {
        // TODO: Address potential duplicates in the dependency list and multiple paths in the provides output
        val pathLookup = mutableMapOf<String, String>()
        val moduleService = project.service<RakuModuleListFetcher>()
        val modules: List<String> = CommaProjectUtil.projectDependencies(project)
        val provides = moduleService.getProvidesListByModules(modules)
        pathLookup.putAll(pathOfProvideReference(provides).entries.map { Pair(it.key, it.value.first()) })
            // TODO: Figure out async for this...
            // pathLookup.putAll(withContext(dispatchScope.coroutineContext, {
            //        pathOfProvideReference(provides).entries.map { Pair(it.key, it.value.first()) }
            // }))
        dependencyState.provideToPath = pathLookup
        dependencyState.loadedDependencies = CommaProjectUtil.projectDependencies(project).toMutableList()
    }

    private fun pathOfProvideReference(reference: String): List<String>? {
        return pathOfProvideReference(listOf(reference))[reference]
    }

    private fun pathOfProvideReference(references: List<String>): Map<String, List<String>> {
        val sdkHome = project.service<RakuProjectSdkService>().sdkPath

        val provideMap: CompletableFuture<Map<String, List<String>>> = CompletableFuture()
        runScope.launch {
            try {
                val locateScript = RakuUtils.getResourceAsFile("scripts/absolute-path-of-module.raku")
                    ?: throw ExecutionException("Resource bundle is corrupted: locate script is missing")
                val pathCollectorScript = RakuCommandLine(sdkHome)
                pathCollectorScript.addParameter(locateScript.absolutePath)
                references.forEach { reference -> pathCollectorScript.addParameter(reference) }
                val output = pathCollectorScript.executeAndRead(null).joinToString("\n")
                provideMap.complete(Json.decodeFromString<Map<String, List<String>>>(output))
            } catch (e: ExecutionException) {
                RakuSdkUtil.reactToSdkIssue(project, "Cannot use current Raku SDK")
                provideMap.complete(mapOf())
            }
        }

        return provideMap.join()
    }

    fun refresh() {
        if (isNotGettingReady) {
            isGettingReady = true
            fillState()
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
    var loadedDependencies by list<String>()
}