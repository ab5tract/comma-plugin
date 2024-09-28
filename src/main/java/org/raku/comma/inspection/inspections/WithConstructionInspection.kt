package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.InspectionConstants.WithConstruction.*
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.UseWithSyntaxFix
import org.raku.comma.psi.*

class WithConstructionInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuConditional) return

        for (branch in element.getBranches()) {
            val description = if (element is RakuIfStatement) DESCRIPTION_WITH else DESCRIPTION_WITHOUT
            if (TERMS.contains(branch.term.text) && checkIfReplaceable((branch.condition))) {
                val start = branch.term.textOffset
                val end = branch.term.textOffset + branch.term.textLength
                holder.registerProblem(branch.condition!!,
                                       description,
                                       ProblemHighlightType.WEAK_WARNING,
                                       UseWithSyntaxFix(branch.term.text, start, end))
            }
        }
    }

    private fun checkIfReplaceable(condition: PsiElement?): Boolean {
        if (condition !is RakuPostfixApplication) return false

        val methodCall = condition.getLastChild() as? RakuMethodCall ?: return false
        return methodCall.callName == ".defined"
    }
}