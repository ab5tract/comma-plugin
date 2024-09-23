package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.psi.RakuRoutineDecl
import org.raku.comma.psi.RakuStatement
import org.raku.comma.utils.RakuPsiUtil

class RemoveUnusedRoutineFix(private val name: String): LocalQuickFix {
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
// TODO: Should we bother with this caretModel stuff?
//        val file: PsiFile = descriptor.psiElement.containingFile
//        val editor = PsiEditorUtil.findEditor(el)
//        val el: PsiElement = file.findElementAt(editor.getCaretModel().getOffset()) ?: return
//        val el = descriptor.psiElement
//        val decl: PsiElement = PsiTreeUtil.getParentOfType(el, RakuRoutineDecl::class.java) ?: return
        val declaration = descriptor.psiElement as RakuRoutineDecl
        RakuPsiUtil.deleteElementDocComments(PsiTreeUtil.getParentOfType(declaration, RakuStatement::class.java))
        val maybeWS = declaration.parent.nextSibling
        if (maybeWS is PsiWhiteSpace) maybeWS.delete()
        declaration.parent.delete()
    }

    override fun getName(): @IntentionName String { return "Safe delete routine '%s'".format(name) }
    override fun getFamilyName(): String { return "Safe delete routine" }
}