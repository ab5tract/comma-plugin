package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.inspection.InspectionConstants.UnitSub.DESCRIPTION
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.RemoveUnitDeclaratorFix
import org.raku.comma.psi.RakuLongName
import org.raku.comma.psi.RakuPackageDecl
import org.raku.comma.psi.RakuRoutineDecl
import org.raku.comma.psi.RakuScopedDecl

class UnitSubInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuScopedDecl) return
        if (element.firstChild !is RakuPackageDecl) return

        val declarator = element.getFirstChild() as RakuPackageDecl
        if (declarator.text == "unit") {
            var next = declarator.nextSibling
            while (next != null) {
                if (next is RakuRoutineDecl) break
                next = next.nextSibling
            }
            if (next == null) return

            val name = PsiTreeUtil.findChildOfType(element, RakuLongName::class.java) ?: return
            if (name.text != "MAIN") {
                holder.registerProblem(element, DESCRIPTION, RemoveUnitDeclaratorFix(declarator.packageKind))
            }
        }
    }
}