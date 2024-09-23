package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.inspection.InspectionConstants.ProblematicReturn.*
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.psi.*

class ProblematicReturnInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element is RakuSubCall) {
            val name = PsiTreeUtil.getChildOfType(element, RakuSubCallName::class.java)
            if (name != null && name.text == "return") {
                val description = checkReturn(holder, element) ?: return
                holder.registerProblem(element, description, ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
            }
        }
    }

    private fun checkReturn(holder: ProblemsHolder, returnCall: PsiElement): String? {
        /* Look for the return target, or something blocking a return. */
        var currentBlockoid: PsiElement? = PsiTreeUtil.getParentOfType(returnCall, RakuBlockoid::class.java)
        while (currentBlockoid != null) {
            var parent = currentBlockoid.parent

            /* If we're in a routine, we're fine. */
            if (parent is RakuRoutineDecl) return null

            /* Return in concurrency constructs is problematic. */
            if (parent is RakuBlock) parent = parent.getParent()

            if (parent is RakuStart)  return DESCRIPTION_START
            if (parent is RakuReact)  return DESCRIPTION_REACT
            if (parent is RakuSupply) return DESCRIPTION_SUPPLY

            if (parent is RakuWheneverStatement) return DESCRIPTION_WHENEVER

            /* Look another block out. */
            currentBlockoid = PsiTreeUtil.getParentOfType(currentBlockoid, RakuBlockoid::class.java)
        }

        /* If we didn't find a return target, then we're outside of any routine. */
        return DESCRIPTION_OUTSIDE
    }
}