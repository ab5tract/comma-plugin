package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import org.raku.comma.psi.RakuElementFactory
import org.raku.comma.psi.RakuInfix

class UseBindingToDestructureFix : LocalQuickFix {
    override fun getFamilyName(): String { return "Use binding to destructure" }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        (descriptor.psiElement as RakuInfix)
            .operator.replace(RakuElementFactory.createInfixOperator(project, ":=").operator)
    }
}