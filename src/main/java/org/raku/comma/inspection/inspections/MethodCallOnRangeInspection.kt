package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.ParenthesizeRangeFix
import org.raku.comma.psi.*

class MethodCallOnRangeInspection : RakuInspection() {
    private val methodCallRegex = Regex("^\\d+\\..+")
    private val description = "Precedence of ^ is looser than method call; please parenthesize"

    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element is RakuPrefixApplication) {
            val maybePrefix: PsiElement = element.getFirstChild() as? RakuPrefix ?: return
            if (!maybePrefix.textMatches("^")) return
            val rest: String = element.text.substring(1)
            if (methodCallRegex.matches(rest)) {
                holder.registerProblem(element, description, ParenthesizeRangeFix())
            }
        } else if (element is RakuInfixApplication && element.isRangeConstructor) {
            val postFixes = element.childrenOfType<RakuPostfixApplication>();
            if (postFixes.isEmpty()) return
            if (postFixes.first().childrenOfType<RakuMethodCall>().isEmpty()) return

             holder.registerProblem(element, description, ParenthesizeRangeFix())
        }
    }
}