package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiEditorUtil
import org.raku.comma.utils.RakuUseUtils

class AddUseModuleFix(private val moduleName: String) : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val editor = PsiEditorUtil.findEditor(descriptor.psiElement)
        val file = descriptor.psiElement.containingFile
        RakuUseUtils.addUse(editor, file, moduleName, moduleName)
    }

    override fun getName(): String { return "Use module '%s'".format(moduleName) }

    override fun getFamilyName(): String { return "Use module" }
}