package org.raku.comma.inspection.fixes

import com.intellij.codeInsight.template.TemplateBuilder
import com.intellij.codeInsight.template.TemplateBuilderImpl
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiEditorUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.util.CommonRefactoringUtil
import org.raku.comma.parsing.RakuElementTypes
import org.raku.comma.psi.*
import org.raku.comma.refactoring.NewCodeBlockData
import org.raku.comma.refactoring.RakuCodeBlockType
import org.raku.comma.refactoring.RakuVariableData
import org.raku.comma.utils.RakuPsiUtil
import org.raku.comma.utils.RakuSignatureUtils

class StubMissingPrivateMethodFix : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val call = descriptor.psiElement as RakuMethodCall
        val name = call.name ?: return
        val packageDecl = PsiTreeUtil.getParentOfType(call, RakuPackageDecl::class.java)
        val list = PsiTreeUtil.findChildOfType(packageDecl, RakuStatementList::class.java)
        val editor = PsiEditorUtil.findEditor(call) ?: return
        if (packageDecl == null || list == null) {
            CommonRefactoringUtil.showErrorHint(
                project,
                editor,
                "Cannot stub private method without enclosing class",
                "Stubbing private method",
                null
            )
            return
        }

        var anchor: PsiElement? = null
        var temp: PsiElement? = call
        while (temp != null && temp !is RakuPackageDecl) {
            temp = temp.parent
            if (temp is RakuRoutineDecl) {
                anchor = temp
            }
        }
        anchor = if (anchor != null)
            PsiTreeUtil.getParentOfType(anchor, RakuStatement::class.java, false)
        else
            RakuPsiUtil.skipSpaces(list.lastChild, false)
        if (anchor == null) {
            CommonRefactoringUtil.showErrorHint(
                project,
                editor,
                "Cannot stub private method: can't find suitable anchor",
                "Stubbing private method",
                null
            )
            return
        }

        if (anchor.lastChild.node.elementType === RakuElementTypes.UNTERMINATED_STATEMENT) {
            RakuPsiUtil.terminateStatement(anchor)
        }

        val parameters: Array<RakuVariableData> = RakuSignatureUtils.populateParameters(call.callArguments).stream()
            .map { n: String? -> RakuVariableData(n, "", false, true) }
            .toList().toTypedArray()

        val data = NewCodeBlockData(
            RakuCodeBlockType.PRIVATEMETHOD,
            "",
            name,
            "",
            parameters
        )

        val newMethod = RakuElementFactory.createNamedCodeBlock(project, data, ArrayList())

        val newlyAddedMethod = list.addAfter(newMethod, anchor)

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.document)
        CodeStyleManager.getInstance(project).reformatNewlyAddedElement(list.node, newlyAddedMethod.node)
        allowRename(newlyAddedMethod, editor)
    }

    private fun allowRename(newMethod: PsiElement, editor: Editor) {
        val children = PsiTreeUtil.findChildrenOfType(newMethod, RakuParameterVariable::class.java)
        val builder: TemplateBuilder = TemplateBuilderImpl(newMethod)
        for (child in children) {
            builder.replaceElement(child, child.name)
        }
        builder.run(editor, true)
    }

    override fun getFamilyName(): String { return "Stub private method" }
}