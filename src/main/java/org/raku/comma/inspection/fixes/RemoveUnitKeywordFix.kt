package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiEditorUtil
import org.raku.comma.parsing.RakuTokenTypes
import org.raku.comma.psi.RakuPackageDecl
import org.raku.comma.psi.RakuScopedDecl

class RemoveUnitKeywordFix(private val unitKeywordOffset: Int) : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
//        val declaration = descriptor.psiElement as RakuPackageDecl
        val scopedDeclaration = descriptor.psiElement as RakuScopedDecl
        val declaration = scopedDeclaration.children[0] as RakuPackageDecl
        val editor = PsiEditorUtil.findEditor(declaration) ?: return
        val endPosition = findEndPosition(declaration) ?: return

        editor.document.deleteString(unitKeywordOffset, endPosition)
    }

    private fun findEndPosition(packageElement: RakuPackageDecl): Int? {
        var child: PsiElement? = packageElement.firstChild
        while (child != null) {
            if (child.node.elementType === RakuTokenTypes.PACKAGE_DECLARATOR) {
                return child.textOffset
            }
            child = child.nextSibling
        }
        return null
    }

    override fun getFamilyName(): String { return "Remove 'unit' declarator" }
}