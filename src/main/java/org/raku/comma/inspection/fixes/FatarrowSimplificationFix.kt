package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.util.childrenOfType
import org.raku.comma.psi.RakuElementFactory
import org.raku.comma.psi.RakuFatArrow

class FatarrowSimplificationFix(private val simplifiedPair: String?) : LocalQuickFix {
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val namedPair = descriptor.psiElement

        if (simplifiedPair != null) {
            namedPair.replace(RakuElementFactory.createColonPair(project, simplifiedPair))
        } else {
            val arrow = namedPair.parent.childrenOfType<RakuFatArrow>().firstOrNull() ?: return
            arrow.replace(RakuElementFactory.createColonPair(project, "%s(%s)".format(arrow.key, arrow.value.text)))
        }
    }

    override fun getName(): String {
        return if (simplifiedPair != null) "Convert to '%s'".format(simplifiedPair) else familyName
    }
    override fun getFamilyName(): String { return "Convert to colonpair" }
}