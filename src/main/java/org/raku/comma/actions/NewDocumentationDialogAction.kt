package org.raku.comma.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import org.raku.comma.utils.RakuUtils
import java.nio.file.Path
import java.nio.file.Paths

class NewDocumentationDialogAction : NewRakuFileDialogAction<NewDocumentationDialog?>() {
    override fun processDialogResult(project: Project?, docPath: String?, dialog: NewDocumentationDialog?) {
        project ?: return
        var docPath = docPath ?: return
        val fileName = dialog?.fileName ?: return

        docPath = stubDoc(Paths.get(docPath), fileName)
        val docFile: VirtualFile? = checkNotNull(LocalFileSystem.getInstance().refreshAndFindFileByPath(docPath))
        FileEditorManager.getInstance(project).openFile(docFile!!, true)
    }

    override fun getDialog(project: Project?, filePath: String?): NewDocumentationDialog {
        return NewDocumentationDialog(project, filePath)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    companion object {
        private fun stubDoc(path: Path, name: String): String {
            var docPath = path.resolve(name)
            // If no extension, add default `.rakudoc`
            if (!name.contains(".")) docPath = Paths.get(path.toString(), "$name.rakudoc")
            val lines: MutableList<String?> = ArrayList<String?>()
            RakuUtils.writeCodeToPath(docPath, lines)
            return docPath.toString()
        }
    }
}
