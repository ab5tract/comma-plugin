package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.InspectionConstants.RakudoImplementationDetails.*
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.psi.RakuMethodCall
import org.raku.comma.psi.RakuSubCallName
import org.raku.comma.psi.external.ExternalRakuRoutineDecl

class RakudoImplementationDetailInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {

        when (element) {
            is RakuSubCallName -> {
                val reference = element.reference ?: return
                val resolution = reference.resolve()
                if (excludeCall(element.callName, resolution)) {
                    holder.registerProblem(element,
                                           DESCRIPTION_FORMAT_ROUTINE.format(element.callName),
                                           ProblemHighlightType.INFORMATION)
                }
            }

            is RakuMethodCall-> {
                val reference = element.reference ?: return
                val resolution = reference.resolve()
                if (excludeCall(element.callName, resolution)) {
                    holder.registerProblem(element,
                                           DESCRIPTION_FORMAT_METHOD.format(element.callName, "method"),
                                           ProblemHighlightType.INFORMATION)
                }
            }

            else -> {}
        }
    }

    private fun excludeCall(callName: String, resolution: PsiElement?): Boolean {
        return (resolution is ExternalRakuRoutineDecl)
                && resolution.isImplementationDetail
                && ! IGNORED_CALLS.contains(callName)
    }
}