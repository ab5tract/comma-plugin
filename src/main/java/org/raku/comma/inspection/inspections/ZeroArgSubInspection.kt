package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.ZeroArgToTermFix
import org.raku.comma.psi.RakuRoutineDecl

class ZeroArgSubInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuRoutineDecl) return
        val name = element.name ?: return
        if (element.isMethod
            || element.params.isNotEmpty()
            || name == "MAIN"
            || name.startsWith("term:<")) return

        val description = "'%s' could be declared as a term operator".format(name)
        holder.registerProblem(element, description, ZeroArgToTermFix(name))
    }
}