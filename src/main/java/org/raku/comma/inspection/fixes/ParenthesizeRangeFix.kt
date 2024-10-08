package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiEditorUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.descendants
import org.raku.comma.psi.*
import kotlin.streams.asStream

class ParenthesizeRangeFix : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element: PsiElement = descriptor.psiElement
        val replacer: String? = when(element) {
                                    is RakuPrefixApplication -> fixPrefixForm(element)
                                    is RakuInfixApplication  -> fixInfixForm(element)
                                    else -> null
                                }
        if (replacer == null) return

        val editor = PsiEditorUtil.findEditor(element) ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.replaceString(element.textOffset,
                                          element.textOffset + element.textLength,
                                          replacer)
        }
    }

    private fun fixInfixForm(infix: RakuInfixApplication): String {
        val postfix = infix.childrenOfType<RakuPostfixApplication>()
        val rangeText = "(%s..%s)".format(infix.operands.first().text,
                                          postfix.first().operand?.firstChild?.text)

        return "%s%s".format(rangeText, methodChainText(infix))
    }

    private fun fixPrefixForm(prefix: RakuPrefixApplication): String? {
        val postFix = prefix.childrenOfType<RakuPostfixApplication>()
        val rangeEnd = postFix.first().operand?.firstChild?.text ?: return null

        return "(^%s)%s".format(rangeEnd, methodChainText(prefix))
    }

    private fun methodChainText(element: PsiElement): String {
        val methodChain = StringBuilder()
        element.descendants().asStream().filter { it is RakuPostfixApplication }
                                        .flatMap { it.childrenOfType<RakuMethodCall>().stream() }
                                        .map { (it as PsiElement).text }
                                        .forEach(methodChain::append)
        return methodChain.toString()
    }

    override fun getFamilyName(): String { return "Parenthesize range constructor" }

}