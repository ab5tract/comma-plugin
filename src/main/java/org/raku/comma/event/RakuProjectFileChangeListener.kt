package org.raku.comma.event

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import org.raku.comma.services.project.RakuMetaDataComponent

abstract class RakuProjectFileChangeListener {
    open fun processFileCreate(event: VFileEvent?) {}
    open fun processDirectoryCreate(event: VFileEvent?) {}
    open fun processFileDelete(event: VFileEvent?) {}
    open fun processDirectoryDelete(event: VFileEvent?) {}
    open fun processFileChange(event: VFileEvent?) {}
    open fun processDirectoryChange(event: VFileEvent?) {}

    abstract fun shouldProcess(event: VFileEvent?): Boolean

    // Common helpers
    fun processDirectoryCreate(event: VFileEvent, directoryName: String?, pathsToWatch: MutableList<String?>) {
        val file: VirtualFile? = checkNotNull(event.getFile())
        if (file!!.getName() == directoryName &&
            (file.findFileByRelativePath("../" + RakuMetaDataComponent.Companion.META6_JSON_NAME) != null
                || file.findFileByRelativePath("../" + RakuMetaDataComponent.Companion.META_OBSOLETE_NAME) != null))
        {
            val fileDirectory = file.toNioPath().toFile()
            pathsToWatch.add(fileDirectory.absolutePath)
        }
    }
}
