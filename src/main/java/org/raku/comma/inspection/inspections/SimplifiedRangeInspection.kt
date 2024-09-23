package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.inspection.InspectionConstants.SimplifiedRange.DESCRIPTION
import org.raku.comma.inspection.InspectionConstants.SimplifiedRange.OPS
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.SimplifiedRangeFix
import org.raku.comma.psi.*

class SimplifiedRangeInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuInfixApplication) return

        val infix: PsiElement = PsiTreeUtil.getChildOfType(element, RakuInfix::class.java) ?: return
        val op = infix.text
        if (! OPS.contains(op)) return

        // Get and check possible siblings
        var rangeEnd = infix.nextSibling
        while (rangeEnd != null) {
            if (checkRangeEnd(rangeEnd)) break
            rangeEnd = rangeEnd.nextSibling
        }

        val rangeStart: PsiElement? = PsiTreeUtil.getPrevSiblingOfType(infix, RakuIntLiteral::class.java)
        var infixInParens: RakuInfixApplication? = null
        if (rangeEnd is RakuParenthesizedExpr) {
            infixInParens = PsiTreeUtil.findChildOfType(rangeEnd, RakuInfixApplication::class.java)
        }
        val shouldAnnotate = rangeStart != null && rangeStart.text == "0" &&  // Basic condition
                // Int condition
                (rangeEnd is RakuIntLiteral && (OPS.contains(op)) ||  // Var condition
                        rangeEnd is RakuVariable && op == "..^" ||  // Infix, possible `0..$n-1`
                        rangeEnd is RakuInfixApplication && op == ".."
                        && PsiTreeUtil.findChildOfType(rangeEnd, RakuWhatever::class.java) == null ||  // Parens, possible ..($n-1)
                        rangeEnd is RakuParenthesizedExpr && op == ".." &&
                        ( // inner parens expr
                                infixInParens != null && checkInfix(infixInParens.children))
                        )
        if (! shouldAnnotate) return

        holder.registerProblem(element, DESCRIPTION, SimplifiedRangeFix())
    }

    private fun checkRangeEnd(rangeEnd: PsiElement): Boolean {
        return rangeEnd is RakuIntLiteral
            || rangeEnd is RakuVariable
            || rangeEnd is RakuInfixApplication
            || rangeEnd is RakuParenthesizedExpr
    }

    private fun checkInfix(children: Array<PsiElement>): Boolean {
        return children.size == 3
            && children[0] is RakuVariable
            && children[1] is RakuInfix && children[1].text == "-"
            && children[2] is RakuIntLiteral && children[2].text == "1"
    }
}