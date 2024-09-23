package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.inspection.InspectionConstants.WheneverOutsideOfReact.DESCRIPTION
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.parsing.RakuTokenTypes
import org.raku.comma.psi.RakuReact
import org.raku.comma.psi.RakuSupply

class WheneverOutsideOfReactInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (! ((element.node.elementType == RakuTokenTypes.STATEMENT_CONTROL) && element.text == "whenever")) {
            return
        }
        val react = PsiTreeUtil.findFirstParent(element, true)
                                    { p: PsiElement? -> p is RakuReact || p is RakuSupply }
        if (react == null) {
            holder.registerProblem(element, DESCRIPTION, ProblemHighlightType.ERROR)
        }
    }
}