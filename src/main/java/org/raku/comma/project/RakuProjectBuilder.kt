package org.raku.comma.project

import com.intellij.ide.highlighter.ModuleFileType
import com.intellij.ide.util.projectWizard.ProjectBuilder
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import org.raku.comma.RakuIcons
import org.raku.comma.module.RakuModuleType
import org.raku.comma.services.project.RakuMetaDataComponent
import org.raku.comma.utils.CommaProjectUtil
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import javax.swing.Icon

class RakuProjectBuilder : ProjectBuilder() {
    private var myUpdate = false
    var fileToImport: String? = null
    private val LOG = Logger.getInstance(javaClass)

    val name: String
        get() = "Raku sources"

    val icon: Icon
        get() = RakuIcons.CAMELIA

    override fun isSuitableSdkType(sdkType: SdkTypeId): Boolean {
        return true
    }

    private fun getModuleFilePath(project: Project): String {
        return "%s%s%s%s".format(
            project.basePath,
            File.separator,
            project.name,
            ModuleFileType.DOT_DEFAULT_EXTENSION
        )
    }

    override fun commit(
        project: Project,
        model: ModifiableModuleModel?,
        modulesProvider: ModulesProvider?
    ): List<Module> {
        try {
            WriteAction.runAndWait<RuntimeException> {
                val lfs = LocalFileSystem.getInstance()
                val metaParentDirectory =   if (fileToImport != null)
                                                Paths.get(fileToImport!!).toString()
                                            else project.basePath.toString()
                val path = FileUtil.toSystemIndependentName(metaParentDirectory)
                val contentRoot = lfs.findFileByPath(path.substring(5)) ?: return@runAndWait

                val modelToPatch = model ?: ModuleManager.getInstance(project).getModifiableModel()
                val module = modelToPatch.newModule(getModuleFilePath(project), RakuModuleType.getInstance().id)

                val rootModel = ModuleRootManager.getInstance(module).modifiableModel
                val entry = rootModel.addContentEntry(contentRoot)
                if (CommaProjectUtil.isRakudoCoreProject(project)) {
                    addSourceDirectory("src", contentRoot, entry, false)

                    addSourceDirectory("t", contentRoot, entry, true)
                    addExcludeDirectory("t/spec", contentRoot, entry)
                    addExcludeDirectory("install", contentRoot, entry)
                    addExcludeDirectory("gen", contentRoot, entry)
                    addExcludeDirectory("blib", contentRoot, entry)

                    // TODO: Create a Java module to add JVM sources? and NQP?
//                    addSourceDirectory("nqp/src", contentRoot, entry, false)
//                    addSourceDirectory("nqp/src/vm/jvm", contentRoot, entry, false)
//                    addSourceDirectory("nqp/MoarVM/src", contentRoot, entry, false)
//                    addSourceDirectory("nqp/t", contentRoot, entry, true)
                } else {
                    addSourceDirectory("lib", contentRoot, entry, false)
                    addSourceDirectory("bin", contentRoot, entry, false)
                    addSourceDirectory("t", contentRoot, entry, true)
                }
                modelToPatch.commit()
                rootModel.commit()

                var metaPath = Paths.get(metaParentDirectory, "META6.json")
                if (! metaPath.toFile().exists()) {
                    metaPath = Paths.get(metaParentDirectory, "META.list")
                }
                val metaFile = lfs.findFileByPath(metaPath.toString())
                if (metaFile != null) {
                    val component = project.service<RakuMetaDataComponent>()
                    component.triggerMetaBuild(metaFile, refreshDependencies = true)
                }

                // Detect and set PM from path
//                val manager = project.service<RakuZefManager>()
//                if (manager != null) {
//                    List<RakuPackageManagerManager.SuggestedItem> list = new ArrayList<>();
//                    RakuPackageManagerManager.detectPMs(list);
//                    if (!list.isEmpty()) {
//                        manager.setPM(list.getFirst().toPM());
//                    }
//                }
            }
        } catch (e: Exception) {
            LOG.info(e)
        }

        return emptyList()
    }

    private fun addSourceDirectory(name: String, contentRoot: VirtualFile, entry: ContentEntry, isTest: Boolean) {
        val child = contentRoot.findChild(name)
        if (child != null && VfsUtilCore.isEqualOrAncestor(entry.url, child.url)) {
            entry.addSourceFolder(child, isTest)
        }
    }

    private fun addExcludeDirectory(name: String, contentRoot: VirtualFile, entry: ContentEntry) {
        // We do not care if you exist, we will exclude you regardless!
        val resolved = contentRoot.toNioPath().resolve(name).toUri().toString()
        entry.addExcludeFolder(resolved)
    }
}
