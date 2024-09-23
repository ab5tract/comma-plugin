package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.InspectionConstants.BuiltinSubmethod.DESCRIPTION_FORMAT
import org.raku.comma.inspection.InspectionConstants.BuiltinSubmethod.SHOULD_BE_SUBMETHOD_NAMES
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.MakeSubmethodFix
import org.raku.comma.psi.RakuRoutineDecl

class BuiltinSubmethodInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuRoutineDecl) return

        val name = element.routineName
        if (! SHOULD_BE_SUBMETHOD_NAMES.contains(name)) return

        if (element.routineKind == "method") {
            holder.registerProblem(element.declaratorNode,
                                   DESCRIPTION_FORMAT.format(name),
                                   ProblemHighlightType.WEAK_WARNING,
                                   MakeSubmethodFix(name))
        }
    }
}