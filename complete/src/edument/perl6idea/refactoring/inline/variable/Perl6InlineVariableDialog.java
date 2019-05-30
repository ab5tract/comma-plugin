package edument.perl6idea.refactoring.inline.variable;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6ParameterVariable;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.refactoring.inline.Perl6InlineDialog;

public class Perl6InlineVariableDialog extends Perl6InlineDialog {
    private final PsiElement myVariableDecl;

    protected Perl6InlineVariableDialog(Project project,
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
        if (myVariableDecl instanceof Perl6VariableDecl)
            variableName = ((Perl6VariableDecl)myVariableDecl).getVariableName();
        else if (myVariableDecl instanceof Perl6ParameterVariable)
            variableName = ((Perl6ParameterVariable)myVariableDecl).getName();
        return "Inline " + variableName;
    }

    @Override
    protected void doAction() {
        invokeRefactoring(
            new Perl6InlineVariableProcessor(
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
