package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiEditorUtil

class ConstKeywordFix : LocalQuickFix {
    override fun getFamilyName(): String {
        return "Use 'constant' keyword"
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val file = descriptor.psiElement.containingFile
        val editor = PsiEditorUtil.findEditor(file) ?: return
        val call = descriptor.psiElement

        editor.document.replaceString(
            call.textOffset,
            call.textOffset + call.textLength,
            "constant"
        )
    }
}