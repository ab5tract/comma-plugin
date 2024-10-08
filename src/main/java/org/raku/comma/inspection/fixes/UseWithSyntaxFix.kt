package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiEditorUtil
import org.raku.comma.psi.RakuPostfixApplication

class UseWithSyntaxFix(replaced: String, private val start: Int, private val end: Int) : LocalQuickFix {

    private val replacer = getReplacer(replaced)

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val condition = descriptor.psiElement as RakuPostfixApplication
        val editor = PsiEditorUtil.findEditor(descriptor.psiElement) ?: return

        // Firstly, delete the `.defined` call
        condition.lastChild.delete()

        // Replace term
        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.replaceString(start, end, replacer)
        }
    }

    private fun getReplacer(text: String): String {
        return when (text) {
            "if" -> "with"
            "elsif" -> "orwith"
            "unless" -> "without"
            else -> "without"
        }
    }

    override fun getName(): String {
        return "Use '%s' syntax construction".format(replacer)
    }

    override fun getFamilyName(): String {
        return "Use equivalent 'with'-style construction"
    }
}