package edument.perl6idea.annotation.fix;

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
import edument.perl6idea.psi.*;
import edument.perl6idea.refactoring.Perl6BlockRenderer;
import edument.perl6idea.utils.Perl6SignatureUtils;
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

        Perl6SubCall call = PsiTreeUtil.getParentOfType(atCaret, Perl6SubCall.class);
        if (call == null)
            return;

        List<Perl6StatementList> scopes = new ArrayList<>();
        PsiElement starter = call;
        while (starter != null) {
            starter = PsiTreeUtil.getParentOfType(starter, Perl6StatementList.class);
            if (starter != null)
                scopes.add((Perl6StatementList) starter);
        }

        List<String> parameters = Perl6SignatureUtils.populateParameters(call.getCallArguments());

        Perl6RoutineDecl decl = Perl6ElementFactory.createRoutineDeclaration(project,
                call.getCallName(), parameters);
        selectScope(project, editor, scopes, decl);
    }

    protected void selectScope(@NotNull Project project, Editor editor, List<Perl6StatementList> scopes, Perl6RoutineDecl decl) {
        if (scopes.size() == 1) {
            invokeWithScope(project, editor, scopes.get(0), decl);
        } else {
            IntroduceTargetChooser.showChooser(editor, scopes, new Pass<Perl6StatementList>() {
                @Override
                public void pass(Perl6StatementList scope) {
                    invokeWithScope(project, editor, scope, decl);
                }
            }, Perl6BlockRenderer::renderBlock, "Select creation scope");
        }
    }

    protected void invokeWithScope(Project project, Editor editor, PsiElement scope, Perl6RoutineDecl decl) {
        WriteCommandAction.runWriteCommandAction(project, "Stub Routine",
                null,
                () -> {
                    PsiElement newDecl = scope.addBefore(decl.getParent(), scope.getFirstChild());
                    PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
                    CodeStyleManager.getInstance(project).reformat(scope);
                    Collection<Perl6ParameterVariable> children = PsiTreeUtil.findChildrenOfType(newDecl, Perl6ParameterVariable.class);
                    TemplateBuilder builder = new TemplateBuilderImpl(newDecl);
                    for (Perl6ParameterVariable var : children)
                        builder.replaceElement(var, var.getName());
                    builder.run(editor, true);
                });
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
