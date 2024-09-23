package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.CaretState
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiEditorUtil
import org.raku.comma.psi.RakuTrait

class ChangeDoesToIsFix : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val editor = PsiEditorUtil.findEditor(descriptor.psiElement.containingFile) ?: return
        val carets: List<CaretState> = editor.caretModel.caretsAndSelections
        (descriptor.psiElement as RakuTrait).changeTraitMod("is")
        editor.caretModel.caretsAndSelections = carets
    }

    override fun getFamilyName(): String {
        return "Replace \"does\" with \"is\""
    }
}