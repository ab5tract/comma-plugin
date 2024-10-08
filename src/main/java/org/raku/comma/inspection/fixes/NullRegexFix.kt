package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiEditorUtil

class NullRegexFix : LocalQuickFix {
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val regexParent = descriptor.psiElement
        val editor = PsiEditorUtil.findEditor(regexParent) ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.replaceString(regexParent.textOffset,
                                  regexParent.textOffset + regexParent.textLength,
                                  "/<?>/")
        }
    }

    override fun getFamilyName(): String { return "Add always successful assertion" }
}