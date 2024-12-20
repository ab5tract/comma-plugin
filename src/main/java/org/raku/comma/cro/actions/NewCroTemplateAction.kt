package org.raku.comma.cro.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import org.raku.comma.cro.template.CroTemplateFileType
import org.raku.comma.utils.Patterns
import org.raku.comma.utils.RakuUtils
import java.nio.file.Path
import java.nio.file.Paths

class NewCroTemplateAction : AnAction() {
    override fun update(event: AnActionEvent) {
        val dataContext = event.dataContext
        val presentation = event.presentation
        val enabled: Boolean = isAvailable(dataContext)
        presentation.setVisible(enabled)
        presentation.setEnabled(enabled)
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData<Project?>(CommonDataKeys.PROJECT) ?: return

        val navigatable: Any? = event.getData<Navigatable?>(CommonDataKeys.NAVIGATABLE)
        var templatePath: String? = null
        if (navigatable != null) {
            if (navigatable is PsiDirectory) templatePath = navigatable.virtualFile.path
            else if (navigatable is PsiFile) {
                val parent = navigatable.parent
                if (parent != null) templatePath = parent.virtualFile.path
            }
        }
        if (templatePath == null) return

        val finalTemplatePath: String? = templatePath
        val validator: InputValidator = object : InputValidator {
            override fun checkInput(inputString: String?): Boolean {
                var string: String = inputString!!
                if (string.indexOf('.') < 0) {
                    string += ".crotmp"
                }
                return !Paths.get(finalTemplatePath, string)
                    .toFile()
                    .exists() && inputString.matches(Patterns.CRO_TEMPLATE_PATTERN.toRegex())
            }

            override fun canClose(inputString: String): Boolean {
                return inputString.matches(Patterns.CRO_TEMPLATE_PATTERN.toRegex())
            }
        }

        var fileName = Messages.showInputDialog(
            project,
            "Cro Template name (type one without an extension to use a default '.crotmp'):",
            "New Cro Template Name",
            Messages.getQuestionIcon(), null, validator
        ) ?: return

        if (fileName.indexOf('.') < 0) {
            fileName += ".crotmp"
        }

        templatePath = stubTemplate(Paths.get(templatePath), fileName)
        val testFile: VirtualFile? = checkNotNull(LocalFileSystem.getInstance().refreshAndFindFileByPath(templatePath))
        FileEditorManager.getInstance(project).openFile(testFile!!, true)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    companion object {
        private fun isAvailable(dataContext: DataContext): Boolean {
            val project = CommonDataKeys.PROJECT.getData(dataContext)
            val navigatable: Any? = CommonDataKeys.NAVIGATABLE.getData(dataContext)
            return project != null
                    && (navigatable is PsiDirectory || navigatable is PsiFile)
        }

        fun stubTemplate(testDirectoryPath: Path, fileName: String): String {
            var testPath = testDirectoryPath.resolve(fileName)
            // If no extension, add default `.crotmp`
            if (!fileName.contains(".")) {
                testPath = Paths.get(
                    testDirectoryPath.toString(),
                    fileName + "." + CroTemplateFileType.INSTANCE.getDefaultExtension()
                )
            }
            RakuUtils.writeCodeToPath(testPath, ArrayList<String?>())
            return testPath.toString()
        }
    }
}
