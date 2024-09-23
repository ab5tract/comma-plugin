package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.raku.comma.psi.RakuElementFactory
import org.raku.comma.psi.RakuWhileStatement

class IdiomaticLoopFix : LocalQuickFix {
    override fun getFamilyName(): String { return "Use idiomatic 'loop' syntax" }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val whileStatement = descriptor.psiElement as? RakuWhileStatement ?: return
        val loop: PsiElement = RakuElementFactory.createLoop(project, whileStatement.getBlock())
        whileStatement.replace(loop)
    }
}