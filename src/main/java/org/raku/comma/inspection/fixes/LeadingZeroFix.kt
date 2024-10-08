package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiEditorUtil

class LeadingZeroFix : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        val editor = PsiEditorUtil.findEditor(element)!!
        val text = element.text

        val replacement = "%so%s".format(text.substring(0, 1), text.substring(1))

        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.replaceString(element.textOffset,
                                          element.textOffset + element.textLength,
                                          replacement)
        }
    }

    override fun getFamilyName(): String { return "Replace with octal representation" }

}