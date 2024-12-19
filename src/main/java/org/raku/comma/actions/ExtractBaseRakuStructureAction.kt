package org.raku.comma.actions

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.RefactoringActionHandler
import com.intellij.refactoring.actions.BasePlatformRefactoringAction
import org.raku.comma.psi.RakuPackageDecl
import org.raku.comma.refactoring.RakuExtractPackageHandler

open class ExtractBaseRakuStructureAction(private val isRole: Boolean) : BasePlatformRefactoringAction() {
    override fun getRefactoringHandler(provider: RefactoringSupportProvider): RefactoringActionHandler? {
        return RakuExtractPackageHandler(isRole)
    }

    override fun isAvailableInEditorOnly(): Boolean = true

    override fun isAvailableOnElementInEditorAndFile(
        element: PsiElement,
        editor: Editor,
        file: PsiFile,
        context: DataContext
    ): Boolean {
        val parent: PsiElement? = PsiTreeUtil.getParentOfType<RakuPackageDecl?>(element, RakuPackageDecl::class.java)
        return parent != null
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        removeFirstWordInMainMenu(this, e)
    }

    companion object {
        fun removeFirstWordInMainMenu(action: AnAction, e: AnActionEvent) {
            if (ActionPlaces.MAIN_MENU == e.place) {
                val templateText = action.templatePresentation.text
                if (templateText != null && templateText.startsWith("Extract")) {
                    e.presentation.text = templateText.substring(templateText.indexOf(' ') + 1)
                }
            }
        }
    }
}
