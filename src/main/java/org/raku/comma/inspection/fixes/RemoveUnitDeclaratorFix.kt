package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiEditorUtil
import org.raku.comma.parsing.RakuTokenTypes

class RemoveUnitDeclaratorFix(private val name: String) : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val packageElement = descriptor.psiElement
        val editor = PsiEditorUtil.findEditor(packageElement) ?: return

        var endOffset: Int = -1
        var child: PsiElement? = packageElement.getFirstChild()
        while (child != null) {
            if (child.getNode().getElementType() === RakuTokenTypes.PACKAGE_DECLARATOR) {
                endOffset = child.textOffset
                break
            }
            child = child.nextSibling
        }

        editor.document.deleteString(packageElement.textOffset, endOffset) // 5 == "unit ".length
    }

    override fun getName(): String { return "Remove 'unit' declarator from defined %s".format(name) }
    override fun getFamilyName(): String { return "Remove 'unit' declarator" }
}