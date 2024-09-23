package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.psi.RakuMethodCall
import org.raku.comma.psi.RakuPostfixApplication

class GrepFirstFix : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement

        val firstCall = (element as RakuPostfixApplication).postfix as RakuMethodCall
        val grepCall  = (element.operand as RakuPostfixApplication).postfix as RakuMethodCall

        grepCall.setName("first")

        val postfixApp = PsiTreeUtil.getParentOfType(grepCall, RakuPostfixApplication::class.java)
        val innerCall = postfixApp?.copy() ?: return
        val outerCall = PsiTreeUtil.getParentOfType(firstCall, RakuPostfixApplication::class.java)

        outerCall?.replace(innerCall)
    }

    override fun getFamilyName(): String { return "Replace .grep.first with .first" }
}