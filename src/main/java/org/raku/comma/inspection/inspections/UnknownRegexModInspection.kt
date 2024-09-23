package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.InspectionConstants.UnknownRegexMod.DESCRIPTION_FORMAT
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.parsing.RakuTokenTypes
import org.raku.comma.psi.RakuRegexModInternal

class UnknownRegexModInspection : RakuInspection() {

    // TODO: Extend to work with the rx:zzz and m:zzz forms
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element is RakuRegexModInternal) {
            if (element.node.findChildByType(RakuTokenTypes.REGEX_MOD_UNKNOWN) != null) {
                holder.registerProblem(element,
                                       DESCRIPTION_FORMAT.format(element.node.text),
                                       ProblemHighlightType.GENERIC_ERROR)
            }
        }
    }
}