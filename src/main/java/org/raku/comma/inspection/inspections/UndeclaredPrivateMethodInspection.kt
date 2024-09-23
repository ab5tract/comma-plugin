package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.inspection.InspectionConstants.UndeclaredPrivateMethod.DESCRIPTION_CANNOT_START_WITH_BANG
import org.raku.comma.inspection.InspectionConstants.UndeclaredPrivateMethod.DESCRIPTION_FORMAT
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.StubMissingPrivateMethodFix
import org.raku.comma.psi.RakuMethodCall
import org.raku.comma.psi.RakuPackageDecl
import org.raku.comma.psi.RakuRoutineDecl
import org.raku.comma.psi.RakuSelf

class UndeclaredPrivateMethodInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuMethodCall) return
        val methodName = element.callName
        val caller = element.getPrevSibling()

        // Annotate only private methods for now
        if (!methodName.startsWith("!")) return

        // We can't safely do this analysis in a role, because the method might be
        // used in a role consumer (and that may even be in another module).
        val enclosingPackage = PsiTreeUtil.getParentOfType(element, RakuPackageDecl::class.java)
        if (enclosingPackage != null && enclosingPackage.packageKind == "role") return

        val reference = element.getReference() as? PsiPolyVariantReference ?: return
        val declaration = reference.multiResolve(false)
        if (declaration.isNotEmpty()) return
        val prev = element.getPrevSibling()
        if (prev is RakuRoutineDecl) {
            holder.registerProblem(element, DESCRIPTION_CANNOT_START_WITH_BANG, ProblemHighlightType.GENERIC_ERROR)
        } else {
            val description = DESCRIPTION_FORMAT.format(methodName)
            if (caller is RakuSelf) {
                holder.registerProblem(element, TextRange(0, methodName.length), description, StubMissingPrivateMethodFix())
            } else {
                holder.registerProblem(element, description, ProblemHighlightType.GENERIC_ERROR)
            }
        }
    }
}