package org.raku.comma.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import org.raku.comma.language.RakuLanguageVersionService
import org.raku.comma.module.builder.RakuModuleBuilderModule
import java.nio.file.Paths

class NewTestDialogAction : NewRakuFileDialogAction<NewTestDialog?>() {
    override fun processDialogResult(project: Project?, testPath: String?, dialog: NewTestDialog?) {
        project ?: return
        var testPath = testPath ?: return
        val fileName = dialog?.testName ?: return

        val service = project.getService<RakuLanguageVersionService>(RakuLanguageVersionService::class.java)
        testPath = RakuModuleBuilderModule.stubTest(
            Paths.get(testPath),
            fileName,
            mutableListOf<String?>(),
            service.version
        )
        val testFile: VirtualFile? = checkNotNull(LocalFileSystem.getInstance().refreshAndFindFileByPath(testPath))
        FileEditorManager.getInstance(project).openFile(testFile!!, true)
    }

    override fun getDialog(project: Project?, filePath: String?): NewTestDialog {
        return NewTestDialog(project, filePath)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
