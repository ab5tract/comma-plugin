package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.StubMissingSubroutineFix
import org.raku.comma.psi.*
import org.raku.comma.psi.impl.*
import org.raku.comma.sdk.RakuSdkType

class UndeclaredOrDeprecatedRoutineInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuSubCallName) return

        // Only do the analysis if the core setting symbols are available.
        val setting = RakuSdkType.getInstance().getCoreSettingFile(element.getProject())
        if (setting.virtualFile.name == "DUMMY") return

        // Resolve the reference.
        val subName = element.callName
        if (subName == "::") return
        val reference = element.reference as PsiReferenceBase.Poly<*> ?: return
        val results = reference.multiResolve(true)

        // If no resolve results, then we've got an error.
        if (results.isEmpty()) {
            // Check whether there is an operator of the same name in scope
            if (element.resolvesAsLexicalOperator()) return

            val description = "Subroutine %s is not declared".format(subName)
            holder.registerProblem(element, description, StubMissingSubroutineFix())
        } else if (results.size == 1) {
            // If it resolves to a type, highlight it as one.
            val resolvedElement = results[0].element
            if (resolvedElement is RakuPackageDecl) {
                resolvedElement.reference?.let { holder.registerProblem(it, ProblemHighlightType.INFORMATION) }
            } else if (resolvedElement is RakuDeprecatable && resolvedElement.isDeprecated) {
                // If it resolves to a deprecated routine, highlight it as one.
                var deprecationMessage = (resolvedElement as RakuDeprecatable).deprecationMessage
                deprecationMessage = if (deprecationMessage == null) "" else " ... use $deprecationMessage"
                val message = "%s is deprecated%s".format(subName, deprecationMessage)
                holder.registerProblem(resolvedElement, message, ProblemHighlightType.WARNING)
            }
        }
    }
}

// TODO: This 'const' -> 'constant' check appears to be pretty expensive check for (in my opinion) a small benefit.
//            if (subName == "const"
//                && (PsiTreeUtil.skipWhitespacesForward(element) is RakuVariable
//                    || PsiTreeUtil.skipWhitespacesForward(element) is RakuInfixApplication)
//            ) {
//                holder.registerProblem(element, description, ConstKeywordFix())