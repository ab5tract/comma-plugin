package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiEditorUtil

class ChangeMyScopeToOurScopeFix(private val name: String?) : LocalQuickFix {
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val editor = PsiEditorUtil.findEditor(descriptor.psiElement) ?: return

        val offset = descriptor.psiElement.textOffset
        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.replaceString(offset, offset + 2, "our")
        }
    }

    override fun getName(): String {
        return  if (name == null) familyName
                else "Change scope of variable '%s' from 'my' to 'our'".format(name)
    }
    override fun getFamilyName(): String { return "Change scope to `our`" }
}