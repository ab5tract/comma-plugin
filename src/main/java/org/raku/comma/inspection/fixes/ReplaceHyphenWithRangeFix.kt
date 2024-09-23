package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.raku.comma.psi.RakuElementFactory

class ReplaceHyphenWithRangeFix : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val delimiter = RakuElementFactory.createRegexRangeDelimiter(project)
        val replace: PsiElement = descriptor.psiElement.replace(delimiter)
        replace.parent.addAfter(delimiter.copy(), replace)
    }

    override fun getFamilyName(): String { return "Replace with range '..'" }
}