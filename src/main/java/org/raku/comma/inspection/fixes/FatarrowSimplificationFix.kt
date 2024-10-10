package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiEditorUtil
import com.intellij.psi.util.childrenOfType
import org.raku.comma.inspection.InspectionConstants.NamedPairArgument.getSimplifiedPair
import org.raku.comma.psi.RakuColonPair
import org.raku.comma.psi.RakuFatArrow

class FatarrowSimplificationFix(private val pairKind: String) : LocalQuickFix {
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val namedPair = descriptor.psiElement
        val editor = PsiEditorUtil.findEditor(namedPair) ?: return
        val willReplace: MutableList<Pair<String, RangeMarker>> = mutableListOf()

        for (pair in namedPair.parent.childrenOfType<RakuColonPair>()) {
            val simplified = generateReplacement(pair) ?: continue
            willReplace.add(Pair(simplified, editor.document.createRangeMarker(pair.textRange)))
        }

        for (arrow in namedPair.parent.childrenOfType<RakuFatArrow>()) {
            willReplace.add(Pair(generateReplacement(arrow), editor.document.createRangeMarker(arrow.textRange)))
        }

        ApplicationManager.getApplication().runWriteAction {
            willReplace.forEach { (text, range) ->
                editor.document.replaceString(range.startOffset, range.endOffset, text)
            }
        }
    }

    private fun generateReplacement(arrow: RakuFatArrow): String {
        return ":" + (getSimplifiedPair(arrow, arrow.key, arrow.value) ?: "%s(%s)".format(arrow.key,
                                                                                          arrow.value.text))
    }

    private fun generateReplacement(pair: RakuColonPair): String? {
        val simplified = getSimplifiedPair(pair, pair.key, pair.statement.firstChild) ?: return null
        return ":$simplified"
    }

    override fun getName(): String {
        return if (pairKind == "ColonPair") "Simplify colon pair" else "Convert to colon pair"
    }

    override fun getFamilyName(): String { return "Convert to colonpair" }
}