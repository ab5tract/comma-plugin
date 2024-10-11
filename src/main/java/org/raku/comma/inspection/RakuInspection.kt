package org.raku.comma.inspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.PsiEditorUtil
import org.raku.comma.psi.external.RakuExternalPsiElement

abstract class RakuInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return RakuPsiElementVisitor(holder, wrapVisitFunction())
    }

    private fun wrapVisitFunction(): (ProblemsHolder, PsiElement) -> Unit  {
        return { innerHolder, element ->
            if (element !is RakuExternalPsiElement) {
                provideVisitFunction(innerHolder, element)
            }
        }
    }

    abstract fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement)

    protected fun customHighlight(element: PsiElement, attributesKey: TextAttributesKey) {
        val editor = PsiEditorUtil.findEditor(element) ?: return
        customHighlight(editor, element.textRange, attributesKey)
    }

    protected fun customHighlight(element: PsiElement, range: TextRange, attributesKey: TextAttributesKey) {
        val editor = PsiEditorUtil.findEditor(element) ?: return
        customHighlight(editor, range, attributesKey)
    }

    protected fun customHighlight(editor: Editor, range: TextRange, attributesKey: TextAttributesKey) {
        customHighlight(editor, range, attributesKey, HighlighterLayer.WEAK_WARNING)
    }

    protected fun customHighlight(editor: Editor, range: TextRange, attributesKey: TextAttributesKey, layer: Int) {
        editor.markupModel.addRangeHighlighter(attributesKey,
                                               range.startOffset,
                                               range.endOffset,
                                               layer,
                                               HighlighterTargetArea.EXACT_RANGE)
    }
}
