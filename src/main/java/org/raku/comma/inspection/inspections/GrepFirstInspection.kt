package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.GrepFirstFix
import org.raku.comma.psi.RakuMethodCall
import org.raku.comma.psi.RakuPostfixApplication

class GrepFirstInspection : RakuInspection() {
    private val description = "Can be simplified into a single first method call"

    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuPostfixApplication) return
        if (element.operand !is RakuPostfixApplication) return

        val innerApplication: RakuPostfixApplication = element.operand as RakuPostfixApplication

        var grepCall: RakuMethodCall? = null
        if (innerApplication.postfix is RakuMethodCall && (innerApplication.postfix as RakuMethodCall).callName == ".grep") {
            grepCall = innerApplication.postfix as RakuMethodCall
        }
        if (grepCall == null) return

        val firstCall = element.postfix as RakuMethodCall // as? RakuMethodCall ?: return

        if (firstCall.callName != ".first") return

        // Firstly, check if first call is arg-less, otherwise we don't
        // need to calculate grep at all
        if (firstCall.callArguments.isNotEmpty() || grepCall.callArguments.size != 1) return

        holder.registerProblem(element, description, GrepFirstFix())
    }
}