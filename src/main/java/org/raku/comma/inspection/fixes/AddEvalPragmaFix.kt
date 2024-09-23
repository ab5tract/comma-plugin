package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiEditorUtil

class AddEvalPragmaFix : LocalQuickFix {
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val file = descriptor.psiElement.containingFile
        val editor = PsiEditorUtil.findEditor(file) ?: return
        editor.document.insertString(0, "use MONKEY-SEE-NO-EVAL;\n");
    }

    override fun getFamilyName(): String { return "Add 'MONKEY-SEE-NO-EVAL' pragma" }
}