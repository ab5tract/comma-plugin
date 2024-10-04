package org.raku.comma.metadata

import com.intellij.execution.ExecutionException
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.*
import com.intellij.openapi.roots.impl.libraries.LibraryEx
import com.intellij.serviceContainer.AlreadyDisposedException
import org.raku.comma.library.RakuLibraryType
import org.raku.comma.sdk.RakuSdkUtil
import org.raku.comma.services.project.RakuProjectSdkService
import org.raku.comma.utils.RakuCommandLine
import org.raku.comma.utils.RakuUtils
import java.util.concurrent.ConcurrentHashMap

@Service(Service.Level.PROJECT)
class RakuProjectModelSync(private val project: Project) {

    fun syncExternalLibraries(module: Module, firstLevelDeps: Set<String>) {
        if (ApplicationManager.getApplication().isUnitTestMode) return

        ApplicationManager.getApplication().invokeLaterOnWriteThread {
            try {
                ModuleRootModificationUtil.updateModel(module) { model: ModifiableRootModel ->
                    val completeMETADependencies: MutableSet<String> = ConcurrentHashMap.newKeySet()
                    completeMETADependencies.addAll(firstLevelDeps)
                    val sdk = obtainSDKAndGatherLibraryDeps(module, firstLevelDeps, completeMETADependencies)
                    val projectModules = getProjectModules(module)
                    val entriesPresentInMETA: MutableSet<String> = HashSet()

                    for (metaDep in completeMETADependencies) {
                        // If local, project module, attach it as dependency
                        if (projectModules.containsKey(metaDep)) {
                            val moduleOfMetaDep = projectModules[metaDep]!!.module
                            if (moduleOfMetaDep != null && ModuleRootManager.getInstance(module).isDependsOn(moduleOfMetaDep)) {
                                entriesPresentInMETA.add(moduleOfMetaDep.name)
                                removeDuplicateEntries(model, moduleOfMetaDep.name)
                            } else if (moduleOfMetaDep != null) {
                                val entry: OrderEntry = model.addModuleOrderEntry(moduleOfMetaDep)
                                entriesPresentInMETA.add(entry.presentableName)
                            }
                        } else {
                            if (sdk == null) continue
                            val maybeLibrary = model.moduleLibraryTable.getLibraryByName(metaDep)
                            entriesPresentInMETA.add(metaDep)
                            if (maybeLibrary == null) {
                                // otherwise create and mark
                                val library = model.moduleLibraryTable.createLibrary(metaDep) as LibraryEx
                                val libraryModel = library.modifiableModel
                                val url = String.format("raku://%d^%s/", sdk.toString().hashCode(), metaDep)
                                libraryModel.kind = RakuLibraryType.LIBRARY_KIND
                                libraryModel.addRoot(url, OrderRootType.SOURCES)
                                val entry = checkNotNull(model.findLibraryOrderEntry(library)) { library }
                                entry.scope = DependencyScope.COMPILE
                                WriteAction.run<RuntimeException> { libraryModel.commit() }
                            } else {
                                removeDuplicateEntries(model, maybeLibrary.name)
                            }
                        }
                    }
                    removeOrderEntriesNotInMETA(model, entriesPresentInMETA)
                }
            } catch (ignore: AlreadyDisposedException) {
                // If the application or the project was closed while we did not finish the job,
                // just ignore it hoping we will be more lucky next time: this sync
                // algorithm should be robust enough not to leave any incurable "leftovers"
                // and if it is so it's better to fix the underlying reasons.
            }
        }
    }

    private fun getProjectModules(module: Module): Map<String?, RakuMetaDataComponent> {
        val modules = ModuleManager.getInstance(project).modules
        val projectModules: MutableMap<String?, RakuMetaDataComponent> = HashMap()
        for (inProjectModule in modules) {
            if (module != inProjectModule) {
                val service = inProjectModule.getService(RakuMetaDataComponent::class.java)!!
                projectModules[service.name] = service
            }
        }
        return projectModules
    }

    private fun obtainSDKAndGatherLibraryDeps(
        module: Module,
        metaDependencies: Set<String>,
        extendedDeps: MutableSet<String>
    ): SdkEntry?
    {
        val sdk = project.service<RakuProjectSdkService>()
        val sdkHomePath = sdk.sdkPath ?: return null
        val sdkVersion = sdk.state.projectSdkVersion ?: return null

        for (dep in metaDependencies) {
            extendedDeps.addAll(collectDependenciesOfModule(module.project, dep, sdkHomePath))
        }
        return SdkEntry(sdkHomePath, sdkVersion)
    }

    private fun removeDuplicateEntries(model: ModifiableRootModel, name: String?) {
        var seen = false
        for (entry in model.orderEntries) {
            if (entry.presentableName == name) {
                if (seen) {
                    model.removeOrderEntry(entry)
                } else {
                    seen = true
                }
            }
        }
    }

    private fun removeOrderEntriesNotInMETA(model: ModifiableRootModel, presentInMeta: Set<String>) {
        for (entry in model.orderEntries) {
            if (entry.isValid && !entry.presentableName.contains("Module source")) {
                if (!(presentInMeta.contains(entry.presentableName))) {
                    model.removeOrderEntry(entry)
                }
            }
        }
    }

    private fun collectDependenciesOfModule(project: Project, metaDep: String, sdk: String): List<String> {
        try {
            val locateScript = RakuUtils.getResourceAsFile("zef/gather-deps.raku")
                ?: throw ExecutionException("Resource bundle is corrupted: locate script is missing")
            val depsCollectorScript = RakuCommandLine(sdk)
            depsCollectorScript.addParameter(locateScript.absolutePath)
            depsCollectorScript.addParameter(metaDep)
            return depsCollectorScript.executeAndRead(locateScript)
        } catch (e: ExecutionException) {
            RakuSdkUtil.reactToSdkIssue(project, "Cannot use current Raku SDK")
            return ArrayList()
        }
    }
}

data class SdkEntry(val path: String, val version: String) {
    override fun toString(): String {
        return "Raku SDK $version"
    }
}
