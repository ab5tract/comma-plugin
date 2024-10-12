package org.raku.comma.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture
import org.raku.comma.dialogs.RakuFileCreationDialog
import org.raku.comma.dialogs.DialogOutput

class NewRakuFileAction : AnAction(), DumbAware {
    override fun actionPerformed(event: AnActionEvent) {
        val project: Project = event.project ?: return

        val virtualFile: VirtualFile? = event.getData(CommonDataKeys.VIRTUAL_FILE)

        val defaultPath: String = if (virtualFile == null || !virtualFile.isDirectory) ""
        else virtualFile.path

        val dialogOutput = CompletableFuture<DialogOutput>()

        if (RakuFileCreationDialog(project, defaultPath, dialogOutput).showAndGet()) {
            // TODO: Notify the user that the file could not be created
            if (!dialogOutput.isCompletedExceptionally) {
                val output = dialogOutput.get()
                val finalPath = output.createdPath
                val finalFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(Paths.get(finalPath).toFile())

                if (finalFile != null && output.openIt) {
                    OpenFileDescriptor(project, finalFile).navigate(true)
                }
            }
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}
