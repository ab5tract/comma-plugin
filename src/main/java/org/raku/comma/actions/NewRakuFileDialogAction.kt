package org.raku.comma.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

abstract class NewRakuFileDialogAction<T : DialogWrapper?> : AnAction() {
    override fun update(event: AnActionEvent) {
        val dataContext = event.dataContext
        val presentation = event.presentation
        val enabled: Boolean = isAvailable(dataContext)
        presentation.setEnabledAndVisible(enabled)
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData<Project?>(CommonDataKeys.PROJECT)
        if (project == null) return

        val navigatable: Any? = event.getData<Navigatable?>(CommonDataKeys.NAVIGATABLE)
        var filePath: String? = null
        if (navigatable != null) {
            if (navigatable is PsiDirectory) filePath = navigatable.virtualFile.path
            else if (navigatable is PsiFile) {
                val parent = navigatable.parent
                if (parent != null) filePath = parent.virtualFile.path
            }
        }
        if (filePath == null) return

        val dialog: T? = getDialog(project, filePath)
        val isOk = dialog!!.showAndGet()
        // User cancelled action
        if (!isOk) return

        processDialogResult(project, filePath, dialog)
    }

    protected abstract fun processDialogResult(project: Project?, path: String?, dialog: T?)

    protected abstract fun getDialog(project: Project?, filePath: String?): T

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    companion object {
        private fun isAvailable(dataContext: DataContext): Boolean {
            val project = CommonDataKeys.PROJECT.getData(dataContext)
            val navigatable: Any? = CommonDataKeys.NAVIGATABLE.getData(dataContext)
            return project != null && (navigatable is PsiDirectory ||
                    navigatable is PsiFile)
        }
    }
}
