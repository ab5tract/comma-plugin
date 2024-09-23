package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiEditorUtil
import org.raku.comma.highlighter.RakuHighlighter
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.psi.PodFormatted

class PodFormatterInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element is PodFormatted) {
            val attributesKey: TextAttributesKey =
                    when (element.getFormatCode()) {
                        "B"  -> RakuHighlighter.POD_TEXT_BOLD
                        "I"  -> RakuHighlighter.POD_TEXT_ITALIC
                        "U"  -> RakuHighlighter.POD_TEXT_UNDERLINE
                        else -> null
                    } ?: return

            val editor = PsiEditorUtil.findEditor(element) ?: return
            customHighlight(editor, element.formattedTextRange, attributesKey, HighlighterLayer.SYNTAX)
        }
    }
}