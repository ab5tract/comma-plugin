package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiEditorUtil

class NoEndPointRangeFix(private val offset: Int) : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        val editor = PsiEditorUtil.findEditor(element) ?: return
        editor.document.insertString(offset, "*")
        editor.caretModel.moveToOffset(offset)
    }

    override fun getFamilyName(): String { return "Add * for infinite range" }
}