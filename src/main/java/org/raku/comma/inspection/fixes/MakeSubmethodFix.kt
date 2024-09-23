package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import org.raku.comma.psi.RakuElementFactory

class MakeSubmethodFix(private val methodName: String) : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val declarator = descriptor.psiElement
        val submethodDeclarator = RakuElementFactory.createRoutineDeclarator(project, "submethod")
        declarator.replace(submethodDeclarator)
    }

    override fun getName(): String { return "Convert '%s' to submethod".format(methodName) }
    override fun getFamilyName(): String { return "Make submethod" }
}