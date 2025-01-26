// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.raku.comma.editor.smartEnter

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.text.CharArrayUtil

class PlainEnterProcessor {
    fun doEnter(editor: Editor, psiElement: PsiElement?) {
        if (processExistingBlankLine(editor, psiElement)) return
        val handler = EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_START_NEW_LINE)
        handler.execute(editor, editor.caretModel.currentCaret, (editor as EditorEx).dataContext)
    }

    companion object {
        /**
         * There is a possible case that target code block already starts with the empty line:
         * <pre>
         * void test(int i) {
         * if (i > 1[caret]) {
         *
         * }
         * }
        </pre> *
         * We want just move caret to correct position at that empty line without creating additional empty line then.
         *
         * @param editor    target editor
         * @param element   target element under caret
         * @return `true` if it was found out that the given code block starts with the empty line and caret
         * is pointed to correct position there, i.e. no additional processing is required;
         * `false` otherwise
         */
        private fun processExistingBlankLine(
            editor: Editor,
            element: PsiElement?
        ): Boolean {
            var whiteSpace: PsiWhiteSpace? = null
            if (element != null) {
                val next = PsiTreeUtil.nextLeaf(element)
                if (next is PsiWhiteSpace) {
                    whiteSpace = next
                }
            }

            if (whiteSpace == null) return false

            val textRange = whiteSpace.textRange
            val document = editor.document
            val whiteSpaceText = document.charsSequence.subSequence(textRange.startOffset, textRange.endOffset)

            if (StringUtil.countNewLines(whiteSpaceText) < 2) return false

            val i = CharArrayUtil.shiftForward(whiteSpaceText, 0, " \t")
            if (i >= whiteSpaceText.length - 1) {
                assert(false) {
                    String.format(
                        "code block: %s, white space: %s",
                        "undefined", whiteSpace.textRange
                    )
                }
                return false
            }

            editor.caretModel.moveToOffset(i + 1 + textRange.startOffset)
            val actionManager = EditorActionManager.getInstance()
            val actionHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_MOVE_LINE_END)
            val dataContext = DataManager.getInstance().getDataContext(editor.component)
            actionHandler.execute(editor, editor.caretModel.currentCaret, dataContext)
            return true
        }
    }
}
