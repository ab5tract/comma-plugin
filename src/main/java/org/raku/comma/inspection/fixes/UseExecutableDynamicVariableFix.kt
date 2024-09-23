package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.psi.RakuElementFactory
import org.raku.comma.psi.RakuPostfixApplication
import org.raku.comma.psi.RakuStrLiteral

class UseExecutableDynamicVariableFix : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val strLiteral = descriptor.psiElement as RakuStrLiteral
        val executableCall = RakuElementFactory.createStatementFromText(project, "$*EXECUTABLE.absolute")
        val postfix = PsiTreeUtil.findChildOfType(executableCall, RakuPostfixApplication::class.java)
        if (postfix != null) strLiteral.replace(postfix)
    }

    override fun getFamilyName(): String { return "Use $*EXECUTABLE.absolute" }
}