package org.raku.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.template.TemplateBuilder;
import com.intellij.codeInsight.template.TemplateBuilderImpl;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.IntroduceTargetChooser;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.*;
import org.raku.refactoring.RakuBlockRenderer;
import org.raku.utils.RakuSignatureUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StubMissingSubroutineFix implements IntentionAction {
    @Override
    public @IntentionName @NotNull String getText() {
        return "Create subroutine";
    }

    @Override
    public @NotNull @IntentionFamilyName String getFamilyName() {
        return "Raku";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        PsiElement atCaret = file.findElementAt(editor.getCaretModel().getOffset());
        if (atCaret == null)
            return;

        RakuSubCall call = PsiTreeUtil.getParentOfType(atCaret, RakuSubCall.class);
        if (call == null)
            return;

        List<RakuStatementList> scopes = new ArrayList<>();
        PsiElement starter = call;
        while (starter != null) {
            starter = PsiTreeUtil.getParentOfType(starter, RakuStatementList.class);
            if (starter != null)
                scopes.add((RakuStatementList) starter);
        }

        List<String> parameters = RakuSignatureUtils.populateParameters(call.getCallArguments());

        RakuRoutineDecl decl = RakuElementFactory.createRoutineDeclaration(project,
                                                                           call.getCallName(), parameters);
        selectScope(project, editor, scopes, decl);
    }

    protected void selectScope(@NotNull Project project, Editor editor, List<RakuStatementList> scopes, RakuRoutineDecl decl) {
        if (scopes.size() == 1) {
            invokeWithScope(project, editor, scopes.get(0), decl);
        } else {
            IntroduceTargetChooser.showChooser(editor, scopes, new Pass<>() {
                @Override
                public void pass(RakuStatementList scope) {
                    invokeWithScope(project, editor, scope, decl);
                }
            }, RakuBlockRenderer::renderBlock, "Select creation scope");
        }
    }

    protected void invokeWithScope(Project project, Editor editor, PsiElement scope, RakuRoutineDecl decl) {
        WriteCommandAction.runWriteCommandAction(project, "Stub Routine",
                null,
                () -> {
                    PsiElement newDecl = scope.addBefore(decl.getParent(), scope.getFirstChild());
                    PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
                    CodeStyleManager.getInstance(project).reformat(scope);
                    Collection<RakuParameterVariable> children = PsiTreeUtil.findChildrenOfType(newDecl, RakuParameterVariable.class);
                    TemplateBuilder builder = new TemplateBuilderImpl(newDecl);
                    for (RakuParameterVariable var : children)
                        builder.replaceElement(var, var.getName());
                    builder.run(editor, true);
                });
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
