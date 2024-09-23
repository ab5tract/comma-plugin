package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import org.raku.comma.psi.RakuElementFactory

class UseDirectAttributeAccessFix(val newName: String) : LocalQuickFix {
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        element.replace(RakuElementFactory.createVariable(project, newName))
    }

    override fun getFamilyName(): String { return "Fix direct attribute access" }
}