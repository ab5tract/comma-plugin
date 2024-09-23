package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.InspectionConstants.NonInheritableComposableDeclaration.DESCRIPTION_FORMAT
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.psi.RakuPackageDecl

class NonInheritableComposableDeclarationInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuPackageDecl) return
        val kind = element.packageKind
        if (kind != "package" && kind != "module") return

        for (trait in element.traits) {

            val description: String? = when (trait.traitModifier) {
                "does"  -> DESCRIPTION_FORMAT.format(element.packageKind, "compose a role")
                "is"    -> DESCRIPTION_FORMAT.format(element.packageKind, "inherit a class")
                else    -> null
            }

            if (description != null) {
                holder.registerProblem(trait, description, ProblemHighlightType.ERROR)
            }
        }
    }
}