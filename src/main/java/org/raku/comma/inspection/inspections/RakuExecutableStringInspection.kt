package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.InspectionConstants.RakuExecutableString.ALIASES
import org.raku.comma.inspection.InspectionConstants.RakuExecutableString.DESCRIPTION
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.UseExecutableDynamicVariableFix
import org.raku.comma.psi.RakuStrLiteral

class RakuExecutableStringInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuStrLiteral) return

        val text = element.stringText
        if (ALIASES.contains(text)) {
            holder.registerProblem(element, DESCRIPTION, UseExecutableDynamicVariableFix())
        }
    }
}