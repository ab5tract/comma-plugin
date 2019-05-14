package edument.perl6idea.refactoring.inline;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.inline.InlineOptionsDialog;
import edument.perl6idea.psi.Perl6RoutineDecl;

public class InlinePerl6RoutineDialog extends InlineOptionsDialog {
    private final Perl6RoutineDecl myRoutineDecl;

    protected InlinePerl6RoutineDialog(Project project, PsiElement element, boolean isInvokedOnReference) {
        super(project, true, element);
        myRoutineDecl = (Perl6RoutineDecl)element;
        myInvokedOnReference = isInvokedOnReference;
        setTitle("Inline Routine");
        init();
    }

    @Override
    protected String getNameLabelText() {
        return String.format("Inline %s %s", myRoutineDecl.getRoutineKind(), myRoutineDecl.getRoutineName());
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
