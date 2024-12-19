package org.raku.comma.event.handlers

import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl
import org.raku.comma.event.RakuProjectFileChangeListener
import org.raku.comma.filetypes.RakuModuleFileType
import org.raku.comma.services.project.RakuMetaDataComponent
import org.raku.comma.utils.RakuUtils
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.regex.Pattern

class RakuModuleFileChangeListener(module: Module) : RakuProjectFileChangeListener() {
    private val modulePaths: MutableList<String> = ArrayList<String>()
    private val project: Project = module.project

    init {
        val entries = ModuleRootManager.getInstance(module).getContentEntries()
        for (entry in entries) {
            val folders = entry.getSourceFolders()
            for (folder in folders) {
                if (!folder.isTestSource()) {
                    val file = folder.getFile()
                    if (file != null && file.getName() == "lib") {
                        modulePaths.add(file.toNioPath().toFile().getAbsolutePath())
                    }
                }
            }
        }
    }

    override fun shouldProcess(event: VFileEvent?): Boolean {
        val file = Objects.requireNonNull<VirtualFile>(event!!.file)
        return event is VFileMoveEvent || event is VFilePropertyChangeEvent ||
                FileTypeManager.getInstance().getFileTypeByFile(file) is RakuModuleFileType ||
                file is VirtualDirectoryImpl
    }

    override fun processDirectoryCreate(event: VFileEvent?) {
        processDirectoryCreate(event!!, "lib", modulePaths.toMutableList())
    }

    override fun processFileDelete(event: VFileEvent?) {
        val file = Objects.requireNonNull<VirtualFile>(event!!.file)
        val oldModuleName = calculateModuleName(file.canonicalPath!!)
        if (oldModuleName != null) {
            updateMetaProvides(oldModuleName, null, null)
        }
    }

    override fun processDirectoryDelete(event: VFileEvent?) {
        val metadata = project.getService<RakuMetaDataComponent>(RakuMetaDataComponent::class.java)
        val file = Objects.requireNonNull<VirtualFile>(event!!.file)
        val path = file.toNioPath()
        for (modulePath in modulePaths) {
            if (path.startsWith(modulePath)) {
                val prefix: String = calculateModulePrefix(Paths.get(modulePath), path)
                for (name in metadata.providedNames) {
                    if (name.startsWith(prefix)) {
                        metadata.removeNamespaceFromProvides(name)
                    }
                }
            }
        }
    }

    override fun processFileChange(event: VFileEvent?) {
        val oldPathRaw = if (event is VFilePropertyChangeEvent)
            event.getOldPath()
        else
            (event as VFileMoveEvent).getOldPath()
        val oldModuleName = calculateModuleName(oldPathRaw)
        val file = Objects.requireNonNull<VirtualFile>(event.getFile())
        val newModuleName = calculateModuleName(file.getCanonicalPath()!!)
        updateMetaProvides(oldModuleName, newModuleName, file.getExtension())
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
        var libPath: Path? = null

        for (modulePath in modulePaths) {
            if (eventPath.startsWith(modulePath)) {
                libPath = Paths.get(modulePath)
                break
            }
        }
        if (libPath == null) return

        val oldPath = Paths.get(stringNewPath.substring(0, stringNewPath.length - newName.length), oldName)

        val metadata = project.getService<RakuMetaDataComponent>(RakuMetaDataComponent::class.java)

        val newPrefix: String = calculateModulePrefix(libPath, eventPath)
        val oldPrefix: String = calculateModulePrefix(libPath, oldPath)
        for (name in metadata.providedNames) {
            if (name.startsWith(oldPrefix)) {
                metadata.removeNamespaceFromProvides(name)
                val ext: String? = oldName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                metadata.addNamespaceToProvides(newPrefix + name.substring(oldPrefix.length), ext)
            }
        }
    }

    private fun processDirectoryMove(event: VFileMoveEvent) {
        val directoryName = event.file.name
        val oldPath = Paths.get(event.oldParent.path, directoryName)
        val newPath = Paths.get(event.oldParent.path, directoryName)
        var libPath: Path? = null

        for (modulePath in modulePaths) {
            if (oldPath.startsWith(modulePath) || newPath.startsWith(modulePath)) {
                libPath = Paths.get(modulePath)
                break
            }
        }
        if (libPath == null) return

        val isFromLib = oldPath.startsWith(libPath)
        val isToLib = newPath.startsWith(libPath)

        val metadata = project.getService<RakuMetaDataComponent>(RakuMetaDataComponent::class.java)

        if (isFromLib && isToLib) {
            val oldPrefix: String = calculateModulePrefix(libPath, oldPath)
            val newPrefix: String = calculateModulePrefix(libPath, newPath)

            for (name in metadata.providedNames) {
                if (name.startsWith(oldPrefix)) {
                    metadata.removeNamespaceFromProvides(name)
                    metadata.addNamespaceToProvides(
                        newPrefix + name.substring(oldPrefix.length),
                        RakuUtils.getNameExtension(name)
                    )
                }
            }
        } else if (isToLib) {
            val file = event.getFile()
            VfsUtilCore.visitChildrenRecursively(file, object : VirtualFileVisitor<Any?>() {
                override fun visitFile(file: VirtualFile): Boolean {
                    val metadata = project.getService<RakuMetaDataComponent>(RakuMetaDataComponent::class.java)
                    if (FileTypeManager.getInstance().getFileTypeByFile(file) is RakuModuleFileType) {
                        metadata.addNamespaceToProvides(calculateModuleName(file.getPath())!!, file.getExtension())
                    }
                    return true
                }
            })
        } else if (isFromLib) {
            val oldPrefix: String = calculateModulePrefix(libPath, oldPath)
            for (name in metadata.providedNames) {
                if (name.startsWith(oldPrefix)) {
                    metadata.removeNamespaceFromProvides(name)
                }
            }
        }
    }

    private fun calculateModuleName(path: String): String? {
        for (modulePath in modulePaths) {
            // FIXME this is still dangerous even with \ escaped
            val m = Pattern.compile(
                String.format(
                    "%s/(.+).(rakumod|pm6)",
                    modulePath.replace("\\\\".toRegex(), "\\\\\\\\")
                )
            )
                .matcher(path)
            if (m.matches()) {
                return m.group(1)
                    .replace("/".toRegex(), "::")
                    .replace("\\\\".toRegex(), "::")
            }
        }
        return null
    }

    private fun updateMetaProvides(oldName: String?, newName: String?, ext: String?) {
        val metadata = project.getService<RakuMetaDataComponent>(RakuMetaDataComponent::class.java)
        if (oldName != null) {
            metadata.removeNamespaceFromProvides(oldName)
        }
        if (newName != null) {
            metadata.addNamespaceToProvides(newName, ext)
        }
    }

    companion object {
        private fun calculateModulePrefix(base: Path, eventDirectoryPath: Path): String {
            val subPath = eventDirectoryPath.subpath(
                base.getNameCount(),
                eventDirectoryPath.getNameCount()
            )
            val joiner = StringJoiner("::")
            for (part in subPath) {
                joiner.add(part.toString())
            }
            return "$joiner::"
        }
    }
}
