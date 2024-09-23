package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.inspection.InspectionConstants.NotProgressingRegex.DESCRIPTION
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.psi.RakuRegexAtom
import org.raku.comma.psi.RakuRegexPsiElement
import org.raku.comma.psi.RakuRegexQuantifier

class NotProgressingRegexInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {

        // Only want quantified regex atoms.
        if (element !is RakuRegexAtom) return
        val quantifier = PsiTreeUtil.getChildOfType(element, RakuRegexQuantifier::class.java) ?: return

        // Should be + or * quantifier.
        val quantText = quantifier.firstChild.text
        if (!(quantText == "+" || quantText == "*")) return

        // See if the atom might match nothing.
        val child = element.getFirstChild()
        if (child is RakuRegexPsiElement && child.mightMatchZeroWidth()) {
            holder.registerProblem(quantifier, DESCRIPTION, ProblemHighlightType.WARNING)
        }
    }
}