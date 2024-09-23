package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiEditorUtil
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.psi.*

class SimplifiedRangeFix : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val application = descriptor.psiElement as RakuInfixApplication

        val element = PsiTreeUtil.findChildOfType(application, RakuInfix::class.java) ?: return
        val editor = PsiEditorUtil.findEditor(element) ?: return
        val startCaretOffset: Int = editor.caretModel.offset

        // Start of `0..foo`
        val offset = application.textOffset

        // Offset of `foo`, last child of application
        val last = application.lastChild
        val offsetEnd = last.textOffset

        val infixText: String = element.getText()
        val isSimpleRange = infixText == ".."
        if (last is RakuIntLiteral && isSimpleRange) {
            // We can not catch exception here, as RakuIntLiteral is always a valid number
            val top = last.text.toInt() + 1
            editor.document.replaceString(offset, offsetEnd + last.getTextLength(), "^$top")
        } else if (infixText == "..^" && (last is RakuIntLiteral || last is RakuVariable)) {
            editor.document.replaceString(offset, offsetEnd, "^")
        } else if (last is RakuInfixApplication && isSimpleRange) {
            handleInfix(editor, offset, last, offsetEnd, last.getTextLength())
        } else if (last is RakuParenthesizedExpr && isSimpleRange) {
            val infixInParens = PsiTreeUtil.findChildOfType(last, RakuInfixApplication::class.java)
            if (infixInParens != null) {
                handleInfix(editor, offset, infixInParens, offsetEnd, last.getTextLength())
            }
        }
        editor.caretModel.moveToOffset(startCaretOffset)
    }

    private fun handleInfix(editor: Editor, offset: Int, last: PsiElement, offsetEnd: Int, length: Int) {
        val children = last.children
        if (checkInfix(children)) {
            editor.document.replaceString(offset, offsetEnd + length, "^" + children[0].text)
        }
    }

    private fun checkInfix(children: Array<PsiElement>): Boolean {
        return children.size == 3
            && children[0] is RakuVariable
            && children[1] is RakuInfix && children[1].text == "-"
            && children[2] is RakuIntLiteral && children[2].text == "1"
    }

    override fun getFamilyName(): String { return "Use simpler range syntax" }
}