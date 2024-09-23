package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiEditorUtil
import org.raku.comma.psi.RakuPackageDecl

class AddUnitKeywordFix : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val packageElement = descriptor.psiElement as RakuPackageDecl
        val editor = PsiEditorUtil.findEditor(descriptor.psiElement) ?: return
        val packageKind: String? = packageElement.packageKind
        val textOffset: Int = packageElement.textOffset - (if (packageKind == null) 0 else packageKind.length + 1)
        editor.document.insertString(textOffset, "unit ")
    }

    override fun getFamilyName(): String { return "Add missing 'unit' scope declaration" }
}