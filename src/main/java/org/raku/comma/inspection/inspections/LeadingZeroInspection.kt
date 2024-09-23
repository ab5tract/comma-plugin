package org.raku.comma.inspection.inspections
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.LeadingZeroFix
import org.raku.comma.psi.RakuIntLiteral

class LeadingZeroInspection : RakuInspection() {
    private val descriptionFormat = "Leading 0 does not indicate octal in Raku: use 0o%s instead"

    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element is RakuIntLiteral) {
            val literal: String = element.getText()
            if (literal.length > 1 && literal.startsWith("0") && Character.isDigit(literal[1])) {
                holder.registerProblem(element,
                                       descriptionFormat.format(literal.substring(1)),
                                       LeadingZeroFix())
            }
        }
    }
}