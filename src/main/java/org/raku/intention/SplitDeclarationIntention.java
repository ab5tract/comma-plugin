package org.raku.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.raku.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class SplitDeclarationIntention extends PsiElementBaseIntentionAction implements IntentionAction {
    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        // Prepare variables necessary for formatting
        PsiFile psiFile = element.getContainingFile();
        RakuVariableDecl originalDecl = PsiTreeUtil.getParentOfType(element, RakuVariableDecl.class);
        if (originalDecl == null) return;
        if (!Objects.equals(originalDecl.getScope(), "my")) return;
        if (originalDecl.getTraits().size() != 0) return;
        int reformatStart = originalDecl.getTextOffset();
        // Create a new
        RakuVariable[] variableNames = originalDecl.getVariables();
        RakuVariableDecl innerDecl = RakuElementFactory.createVariableDecl(project, originalDecl.getScope(), variableNames[0].getVariableName());
        PsiElement newDeclaration = PsiTreeUtil.getParentOfType(innerDecl, RakuScopedDecl.class);
        PsiElement newAssignment = RakuElementFactory.createInfixApplication(project, "=", Arrays.asList(variableNames[0], originalDecl.getInitializer()));
        // Prepare nodes to modify
        PsiElement parent = PsiTreeUtil.getParentOfType(originalDecl, RakuStatementList.class);
        PsiElement anchor = PsiTreeUtil.getParentOfType(originalDecl, RakuStatement.class);
        if (parent == null || anchor == null) return;
        // Modify
        PsiElement end = parent.addAfter(RakuElementFactory.createStatementFromText(project, newAssignment.getText() + ";"), anchor);
        parent.addAfter(RakuElementFactory.createStatementFromText(project, newDeclaration.getText() + ";"), anchor);
        anchor.delete();
        // Apply styling
        CodeStyleManager.getInstance(project).reformatText(psiFile, reformatStart, end.getTextOffset() + end.getTextLength());
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        RakuVariableDecl declaration = PsiTreeUtil.getParentOfType(element, RakuVariableDecl.class);
        if (declaration == null || !declaration.hasInitializer()) return false;
        if (!Objects.equals(declaration.getScope(), "my")) return false;
        if (declaration.getTraits().size() != 0) return false;

        RakuVariable[] variables = declaration.getVariables();

        return variables.length == 1 && variables[0].getVariableName() != null &&
               RakuVariable.getTwigil(variables[0].getVariableName()) == ' ';
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
