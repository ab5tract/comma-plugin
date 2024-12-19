package org.raku.comma.event

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.*
import com.intellij.serviceContainer.AlreadyDisposedException
import org.raku.comma.event.handlers.RakuModuleFileChangeListener
import org.raku.comma.event.handlers.RakuResourceFileChangeListener

class ModuleMetaChangeListener(private val myModule: Module) : BulkFileListener {
    private val myListeners: Array<RakuProjectFileChangeListener>?

    init {
        val conn = ApplicationManager.getApplication().getMessageBus().connect()
        conn.subscribe<BulkFileListener>(VirtualFileManager.VFS_CHANGES, this)

        myListeners = arrayOf<RakuProjectFileChangeListener>(
            RakuModuleFileChangeListener(myModule),
            RakuResourceFileChangeListener(myModule)
        )
    }

    override fun before(list: MutableList<out VFileEvent>) {}

    override fun after(events: MutableList<out VFileEvent>) {
        if (myListeners == null) return

        for (event in events) {
            val file = event.getFile()
            if (file == null) continue

            for (listener in myListeners) {
                if (listener.shouldProcess(event)) {
                    if (event is VFileCreateEvent) {
                        if (file.isDirectory()) listener.processDirectoryCreate(event)
                        else listener.processFileCreate(event)
                    } else if (event is VFileDeleteEvent) {
                        if (file.isDirectory()) listener.processDirectoryDelete(event)
                        else listener.processFileDelete(event)
                    } else if (event is VFilePropertyChangeEvent
                        && event.propertyName == VirtualFile.PROP_NAME || event is VFileMoveEvent)
                    {
                        if (file.isDirectory) listener.processDirectoryChange(event)
                        else listener.processFileChange(event)
                    } else {
                        // If it another type of event, we don't want to update LocalFileSystem
                        continue
                    }

                    if (!ApplicationManager.getApplication().isUnitTestMode()) {
                        ApplicationManager.getApplication().invokeLater(
                            Runnable {
                                try {
                                    ProjectView.getInstance(myModule.project).refresh()
                                    LocalFileSystem.getInstance().refresh(false)
                                } catch (ignored: AlreadyDisposedException) {
                                    // If we tried to refresh and the project is already disposed - forget about it
                                }
                            })
                    }
                }
            }
        }
    }
}
