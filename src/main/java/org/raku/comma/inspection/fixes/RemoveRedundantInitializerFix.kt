package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import org.raku.comma.psi.RakuElementFactory
import org.raku.comma.psi.RakuVariableDecl

class RemoveRedundantInitializerFix : LocalQuickFix {
    override fun getFamilyName(): String { return  "Remove redundant initializer" }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val variableDecl = descriptor.psiElement as RakuVariableDecl

        // length of variableDecl.variableNames should already have been checked in any inspection that calls this fix
        val newDecl = RakuElementFactory.createVariableDecl(project, variableDecl.scope, variableDecl.variableNames[0])
        variableDecl.replace(newDecl)
    }
}