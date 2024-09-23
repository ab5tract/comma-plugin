package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.inspection.InspectionConstants.UselessMethodDeclaration.DESCRIPTION_FORMAT
import org.raku.comma.inspection.InspectionConstants.UselessMethodDeclaration.DESCRIPTION_PACKAGE
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.psi.RakuPackageDecl
import org.raku.comma.psi.RakuRoutineDecl

class UselessMethodDeclarationInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element is RakuRoutineDecl) {
            // Check that we've got a method.
            val kind = element.routineKind
            if (kind != "method" && kind != "submethod") return

            // Check it is has-scoped (the default for methods) and has a name.
            if (element.scope != "has") return
            val nameIdentifier = element.nameIdentifier ?: return

            // Potentially useless; see if it's in a package.
            val packageDecl = PsiTreeUtil.getParentOfType(element, RakuPackageDecl::class.java)
            var canHaveMethods = false
            if (packageDecl != null) {
                val packageKind = packageDecl.packageKind
                canHaveMethods = !(packageKind == "module" || packageKind == "package")
            }
            if (! canHaveMethods) {
                val description =   if (packageDecl == null)
                                        DESCRIPTION_PACKAGE
                                    else
                                        DESCRIPTION_FORMAT.format(packageDecl.packageKind)
                holder.registerProblem(nameIdentifier, description, ProblemHighlightType.WARNING)
            }
        }
    }
}