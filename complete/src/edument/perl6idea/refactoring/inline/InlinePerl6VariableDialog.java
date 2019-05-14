package edument.perl6idea.refactoring.inline;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.inline.InlineOptionsDialog;
import edument.perl6idea.psi.Perl6VariableDecl;

public class InlinePerl6VariableDialog extends InlineOptionsDialog {
    private final PsiNamedElement myVariableDecl;

    protected InlinePerl6VariableDialog(Project project, PsiNamedElement variableDecl, boolean isInvokedOnReference) {
        super(project, true, variableDecl);
        myVariableDecl = variableDecl;
        myInvokedOnReference = isInvokedOnReference;
        setTitle("Inline Variable");
        init();
    }

    @Override
    protected String getNameLabelText() {
        return "Inline " + myVariableDecl.getName();
    }

    @Override
    protected String getBorderTitle() {
        return "Inline";
    }

    @Override
    protected String getInlineAllText() {
        return "Inline all and remove the declaration";
    }

    @Override
    protected String getInlineThisText() {
        return "Inline this only and keep the declaration";
    }

    @Override
    protected boolean isInlineThis() {
        return false;
    }

    @Override
    protected void doAction() {
        close(OK_EXIT_CODE);
    }
}
