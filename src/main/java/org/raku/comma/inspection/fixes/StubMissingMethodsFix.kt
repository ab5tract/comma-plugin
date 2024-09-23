package org.raku.comma.inspection.fixes

import com.intellij.codeInsight.intention.FileModifier.SafeFieldForPreview
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import com.intellij.psi.util.PsiEditorUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.util.CommonRefactoringUtil
import org.raku.comma.psi.RakuElementFactory
import org.raku.comma.psi.RakuPackageDecl
import org.raku.comma.psi.RakuStatementList

class StubMissingMethodsFix(@SafeFieldForPreview val toImplement: List<String>) : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val declaration = descriptor.psiElement as RakuPackageDecl
        val editor = PsiEditorUtil.findEditor(declaration) ?: return

        val statementList = PsiTreeUtil.findChildOfType(declaration, RakuStatementList::class.java)

        if (statementList == null) {
            showErrorHint(project, editor)
            return
        }

        for (methodDef in toImplement) {
            val methodDecl = RakuElementFactory.createStatementFromText(project, methodDef)
            statementList.node.addChild(methodDecl.node)
            statementList.node.addChild(PsiWhiteSpaceImpl("\n"))
        }
        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.document)
        CodeStyleManager.getInstance(project).reformat(declaration)

    }

    private fun showErrorHint(project: Project, editor: Editor) {
        CommonRefactoringUtil.showErrorHint(
            project,
            editor,
            "Cannot stub methods without package body",
            "Stubbing Role Methods",
            null
        )
    }

    override fun getFamilyName(): String {
        return "Stub missing methods"
    }
}