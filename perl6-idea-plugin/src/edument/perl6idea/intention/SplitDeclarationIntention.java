package edument.perl6idea.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SplitDeclarationIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        // Prepare variables necessary for formatting
        PsiFile psiFile = element.getContainingFile();
        Perl6VariableDecl originalDecl = PsiTreeUtil.getParentOfType(element, Perl6VariableDecl.class);
        if (originalDecl == null) return;
        int reformatStart = originalDecl.getTextOffset();
        // Create a new
        Perl6Variable[] variableNames = originalDecl.getVariables();
        Perl6VariableDecl innerDecl = Perl6ElementFactory.createVariableDecl(project, originalDecl.getScope(), variableNames[0].getVariableName());
        PsiElement newDeclaration = PsiTreeUtil.getParentOfType(innerDecl, Perl6ScopedDecl.class);
        PsiElement newAssignment = Perl6ElementFactory.createInfixApplication(project, "=", Arrays.asList(variableNames[0], originalDecl.getInitializer()));
        // Prepare nodes to modify
        PsiElement parent = PsiTreeUtil.getParentOfType(originalDecl, Perl6StatementList.class);
        PsiElement anchor = PsiTreeUtil.getParentOfType(originalDecl, Perl6Statement.class);
        if (parent == null || anchor == null) return;
        // Modify
        PsiElement end = parent.addAfter(Perl6ElementFactory.createStatementFromText(project, newAssignment.getText() + ";"), anchor);
        parent.addAfter(Perl6ElementFactory.createStatementFromText(project, newDeclaration.getText() + ";"), anchor);
        anchor.delete();
        // Apply styling
        CodeStyleManager.getInstance(project).reformatText(psiFile, reformatStart, end.getTextOffset() + end.getTextLength());
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        Perl6VariableDecl declaration = PsiTreeUtil.getParentOfType(element, Perl6VariableDecl.class);
        if (declaration == null || !declaration.hasInitializer()) return false;

        Perl6Variable[] variables = declaration.getVariables();

        return variables.length == 1 && variables[0].getVariableName() != null;
    }

    @Override
    public @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String getFamilyName() {
        return "Split into declaration and assignment";
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getText() {
        return getFamilyName();
    }
}
