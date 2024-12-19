package org.raku.comma.event.handlers

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent
import org.raku.comma.event.RakuProjectFileChangeListener
import org.raku.comma.services.project.RakuMetaDataComponent
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.regex.Pattern

class RakuResourceFileChangeListener(module: Module) : RakuProjectFileChangeListener() {
    private val resourcePaths: MutableList<String> = ArrayList<String>()
    private val project: Project = module.project

    init {
        val data = project.getService<RakuMetaDataComponent>(RakuMetaDataComponent::class.java)
        val metaVFile = data.metaFile
        if (metaVFile != null) {
            val resourcesFile = metaVFile.toNioPath().resolveSibling("resources").toFile()
            if (resourcesFile.exists()) resourcePaths.add(resourcesFile.getAbsolutePath())
        }
    }

    override fun shouldProcess(event: VFileEvent?): Boolean {
        val file = Objects.requireNonNull<VirtualFile>(event!!.file)
        if (file.isDirectory() && file.getName() == "resources" ||
            event is VFileMoveEvent || event is VFilePropertyChangeEvent
        ) return true
        for (resourcePath in resourcePaths) {
            if (event.path.startsWith(resourcePath)) return true
        }
        return false
    }

    override fun processFileCreate(event: VFileEvent?) {
        val file = event!!.file
        if (file == null) return
        val newResource = calculateResourceName(file.canonicalPath!!)
        if (newResource != null) {
            updateMetaResources(null, newResource)
        }
    }

    override fun processDirectoryCreate(event: VFileEvent?) {
        processDirectoryCreate(event!!, "resources", resourcePaths.toMutableList())
    }

    override fun processFileDelete(event: VFileEvent?) {
        val file = Objects.requireNonNull<VirtualFile>(event!!.file)
        val oldResource = calculateResourceName(file.canonicalPath!!)
        if (oldResource != null) updateMetaResources(oldResource, null)
    }

    override fun processDirectoryDelete(event: VFileEvent?) {
        val file = Objects.requireNonNull<VirtualFile>(event!!.file)
        val metadata = project.getService<RakuMetaDataComponent>(RakuMetaDataComponent::class.java)

        // If it's the resources directory itself, purify everything!
        if (file.getName() == "resources" && resourcePaths.contains(file.getPath())) {
            for (resource in metadata.resources) {
                metadata.removeResource(resource)
            }
            return
        }
        // Otherwise the normal procedure, remove everything
        // residing in this directory
        val path = file.toNioPath()
        for (resourcePath in resourcePaths) {
            if (path.startsWith(resourcePath)) {
                val prefix: String = calculateResourcePrefix(Paths.get(resourcePath), path)
                for (name in metadata.resources) {
                    if (name.startsWith(prefix)) metadata.removeResource(name)
                }
            }
        }
    }

    override fun processFileChange(event: VFileEvent?) {
        val oldPathRaw =
            if (event is VFilePropertyChangeEvent) event.getOldPath() else (event as VFileMoveEvent).getOldPath()
        val newPathRaw =
            if (event is VFilePropertyChangeEvent) event.getNewPath() else (event as VFileMoveEvent).getNewPath()
        val oldResourceName = calculateResourceName(oldPathRaw)
        val newResourceName = calculateResourceName(newPathRaw)
        updateMetaResources(oldResourceName, newResourceName)
    }

    override fun processDirectoryChange(event: VFileEvent?) {
        if (event is VFileMoveEvent) {
            processDirectoryMove(event)
        } else if (event is VFilePropertyChangeEvent) {
            processDirectoryRename(event)
        }
    }

    private fun processDirectoryRename(event: VFilePropertyChangeEvent) {
        val oldName = event.getOldValue() as String
        val newName = event.getNewValue() as String
        val stringNewPath = event.getPath()
        val eventPath = Paths.get(stringNewPath)
        var resourcesPath: Path? = null

        for (resourcePath in resourcePaths) {
            if (eventPath.startsWith(resourcePath)) {
                resourcesPath = Paths.get(resourcePath)
                break
            }
        }
        if (resourcesPath == null) return

        val oldPath = Paths.get(stringNewPath.substring(0, stringNewPath.length - newName.length), oldName)

        val metadata = project.getService<RakuMetaDataComponent>(RakuMetaDataComponent::class.java)

        val newPrefix: String = calculateResourcePrefix(resourcesPath, eventPath)
        val oldPrefix: String = calculateResourcePrefix(resourcesPath, oldPath)
        for (name in metadata.resources) {
            if (name.startsWith(oldPrefix)) {
                metadata.removeResource(name)
                metadata.addResource(newPrefix + name.substring(oldPrefix.length))
            }
        }
    }

    private fun processDirectoryMove(event: VFileMoveEvent) {
        val directoryName = event.getFile().getName()
        val oldPath = Paths.get(event.getOldParent().getPath(), directoryName)
        val newPath = Paths.get(event.getNewParent().getPath(), directoryName)
        var resourcesPath: Path? = null

        for (resourcePath in resourcePaths) {
            if (oldPath.startsWith(resourcePath) || newPath.startsWith(resourcePath)) {
                resourcesPath = Paths.get(resourcePath)
                break
            }
        }
        if (resourcesPath == null) return

        val isFromResources = oldPath.startsWith(resourcesPath)
        val isToResources = newPath.startsWith(resourcesPath)

        val metadata = project.getService<RakuMetaDataComponent>(RakuMetaDataComponent::class.java)

        if (isFromResources && isToResources) {
            val oldPrefix: String = calculateResourcePrefix(resourcesPath, oldPath)
            val newPrefix: String = calculateResourcePrefix(resourcesPath, newPath)

            for (name in metadata.resources) {
                if (name.startsWith(oldPrefix)) {
                    metadata.removeResource(name)
                    metadata.addResource(newPrefix + name.substring(oldPrefix.length))
                }
            }
        } else if (isToResources) {
            val file = event.getFile()
            VfsUtilCore.visitChildrenRecursively(file, object : VirtualFileVisitor<Any?>() {
                override fun visitFile(file: VirtualFile): Boolean {
                    if (!file.isDirectory()) {
                        val resourceName = calculateResourceName(file.getPath())
                        if (!metadata.resources.contains(resourceName)) {
                            metadata.addResource(resourceName!!)
                        }
                    }
                    return true
                }
            })
        } else if (isFromResources) {
            val oldPrefix: String = calculateResourcePrefix(resourcesPath, oldPath)
            for (name in metadata.resources) if (name.startsWith(oldPrefix)) metadata.removeResource(name)
        }
    }

    private fun calculateResourceName(path: String): String? {
        for (resourcePath in resourcePaths) {
            val m = Pattern.compile(String.format("%s/(.+)", resourcePath)).matcher(path)
            if (m.matches()) return m.group(1)
        }
        return null
    }

    private fun updateMetaResources(oldName: String?, newName: String?) {
        val metadata = project.getService<RakuMetaDataComponent>(RakuMetaDataComponent::class.java)

        if (oldName != null) {
            metadata.removeResource(oldName)
        }
        if (newName != null) {
            metadata.addResource(newName)
        }
    }

    companion object {
        private fun calculateResourcePrefix(base: Path, eventDirectoryPath: Path): String {
            val subPath = eventDirectoryPath.subpath(
                base.getNameCount(),
                eventDirectoryPath.getNameCount()
            )
            return subPath.toString() + "/"
        }
    }
}
