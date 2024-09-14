package org.raku.comma.refactoring.inline.variable;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.raku.comma.psi.RakuParameterVariable;
import org.raku.comma.psi.RakuVariableDecl;
import org.raku.comma.refactoring.inline.RakuInlineDialog;

public class RakuInlineVariableDialog extends RakuInlineDialog {
    private final PsiElement myVariableDecl;

    protected RakuInlineVariableDialog(Project project,
                                       PsiElement variableDecl,
                                       PsiElement usage,
                                       Editor editor, boolean invokedOnDeclaration) {
        super(project, variableDecl, usage, editor);
        myVariableDecl = variableDecl;
        myInvokedOnReference = !invokedOnDeclaration;
        setTitle("Inline Variable");
        init();
    }

    @Override
    protected String getNameLabelText() {
        String variableName = "unrecognized element";
        if (myVariableDecl instanceof RakuVariableDecl)
            variableName = ((RakuVariableDecl)myVariableDecl).getVariableNames()[0];
        else if (myVariableDecl instanceof RakuParameterVariable)
            variableName = ((RakuParameterVariable)myVariableDecl).getName();
        return "Inline " + variableName;
    }

    @Override
    protected void doAction() {
        invokeRefactoring(
            new RakuInlineVariableProcessor(
                myProject, myVariableDecl, myReference, myEditor,
                isInlineThisOnly(), !isKeepTheDeclaration())
        );
    }

    @Override
    public boolean isInlineThisOnly() {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return !PsiTreeUtil.isAncestor(myVariableDecl, myReference, false);
        }
        return super.isInlineThisOnly();
    }

    @Override
    public boolean isKeepTheDeclaration() {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return !PsiTreeUtil.isAncestor(myVariableDecl, myReference, false);
        }
        return super.isKeepTheDeclaration();
    }
}
