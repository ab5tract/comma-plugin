package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiEditorUtil
import com.intellij.psi.util.childrenOfType
import org.raku.comma.inspection.InspectionConstants.NamedPairArgument.getSimplifiedPair
import org.raku.comma.psi.RakuElementFactory
import org.raku.comma.psi.RakuFatArrow

class FatarrowSimplificationFix(private val simplifiedPair: String?) : LocalQuickFix {
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val namedPair = descriptor.psiElement
        val editor = PsiEditorUtil.findEditor(namedPair) ?: return

        ApplicationManager.getApplication().runWriteAction {
            if (simplifiedPair != null) {
                namedPair.replace(RakuElementFactory.createColonPair(project, simplifiedPair))
            } else {
                val toReplace = namedPair.parent.childrenOfType<RakuFatArrow>()
                val replaceLocations = toReplace.map { editor.document.createRangeMarker(it.textRange) }
                (toReplace.withIndex()).forEach { a ->
                    val offset = replaceLocations[a.index]
                    val replacement = generateReplacement(a.value)
                    editor.document.replaceString(offset.startOffset, offset.endOffset, replacement)
                }
            }
        }
    }

    private fun generateReplacement(arrow: RakuFatArrow): String {
        return ":" + (getSimplifiedPair(arrow, arrow.key, arrow.value) ?: "%s(%s)".format(arrow.key,
                                                                                          arrow.value.text))
    }

    override fun getName(): String {
        return if (simplifiedPair != null) "Convert to '%s'".format(simplifiedPair) else familyName
    }
    override fun getFamilyName(): String { return "Convert to colonpair" }
}