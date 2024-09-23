package org.raku.comma.inspection.fixes

import com.intellij.codeInsight.template.TemplateBuilder
import com.intellij.codeInsight.template.TemplateBuilderImpl
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pass
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.util.PsiEditorUtil
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.IntroduceTargetChooser
import org.raku.comma.psi.*
import org.raku.comma.refactoring.RakuBlockRenderer
import org.raku.comma.utils.RakuSignatureUtils

class StubMissingSubroutineFix : LocalQuickFix {
    private val commandName = "Stub Routine"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val file = descriptor.psiElement.containingFile
        val call = descriptor.psiElement
        val editor = PsiEditorUtil.getInstance().findEditorByPsiElement(call) ?: return

        val decl: RakuRoutineDecl = buildDeclaration(project, file, editor) ?: return

        val scopes: MutableList<RakuStatementList> = ArrayList()
        var starter: PsiElement? = call
        while (starter != null) {
            starter = PsiTreeUtil.getParentOfType(starter, RakuStatementList::class.java)
            if (starter != null) scopes.add(starter)
        }

        writeWithScope(project, editor, scopes, decl)
    }

    private fun writeWithScope(project: Project, editor: Editor, scopes: List<RakuStatementList>, decl: RakuRoutineDecl) {
        if (scopes.size == 1) {
            writeStubDeclaration(project, editor, scopes.first(), decl)
        } else {
            val pass = object : Pass<RakuStatementList>() {
                override fun pass(scope: RakuStatementList) {
                    writeStubDeclaration(project, editor, scope, decl)
                }
            }
            IntroduceTargetChooser.showChooser(editor, scopes, pass, RakuBlockRenderer::renderBlock, "Select Creation Scope")
        }
    }

    private fun writeStubDeclaration(project: Project, editor: Editor, scope: RakuStatementList, decl: RakuRoutineDecl) {
        val writeFunction = {
            val newDecl: PsiElement = scope.addBefore(decl.parent, scope.firstChild)
            PsiDocumentManager.getInstance(project)
                              .doPostponedOperationsAndUnblockDocument(editor.document)

            CodeStyleManager.getInstance(project).reformat(scope)
            val children = PsiTreeUtil.findChildrenOfType(
                newDecl,
                RakuParameterVariable::class.java
            )
            val builder: TemplateBuilder = TemplateBuilderImpl(newDecl)
            for (param in children) builder.replaceElement(param, param.name)
            val body = PsiTreeUtil.findChildrenOfType(
                newDecl,
                RakuBlockoid::class.java
            )
            builder.replaceElement(body.first(), "{ ... }")
            builder.run(editor, true)
        }
        WriteCommandAction.runWriteCommandAction(
            project,
            commandName,
            null,
            writeFunction
        )
    }

    private fun buildDeclaration(project: Project, file: PsiFile, editor: Editor) : RakuRoutineDecl? {
        val atCaret: PsiElement = file.findElementAt(editor.caretModel.offset) ?: return null
        val call = PsiTreeUtil.getParentOfType(atCaret, RakuSubCall::class.java) ?: return null

        val parameters = RakuSignatureUtils.populateParameters(call.getCallArguments())
        return RakuElementFactory.createRoutineDeclaration(
            project,
            call.getCallName(),
            parameters
        )
    }

    override fun getFamilyName(): String { return "Create routine definition" }
}

