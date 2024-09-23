package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.InspectionConstants.MyScopedVariableExported.DESCRIPTION
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.ChangeMyScopeToOurScopeFix
import org.raku.comma.psi.RakuScopedDecl
import org.raku.comma.psi.RakuVariableDecl

class MyScopedVariableExportedInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuScopedDecl) return

        if (element.scope != "my") return
        if (element.getLastChild() !is RakuVariableDecl) return

        val variableDecl: RakuVariableDecl = element.lastChild as RakuVariableDecl
        if (!variableDecl.isExported) return

        // If variable is exported and the scope is `my`, annotate
        holder.registerProblem(element, DESCRIPTION, ChangeMyScopeToOurScopeFix(variableDecl.name))
    }
}