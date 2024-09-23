package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.InspectionConstants.NullRegex.DESCRIPTION
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.NullRegexFix
import org.raku.comma.psi.RakuRegexDriver

class NullRegexInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element is RakuRegexDriver && element.getText().isEmpty()) {
            holder.registerProblem(element.parent, DESCRIPTION, NullRegexFix())
        }
    }
}