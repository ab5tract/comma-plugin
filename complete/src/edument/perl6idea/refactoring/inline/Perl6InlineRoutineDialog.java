package edument.perl6idea.refactoring.inline;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.inline.InlineOptionsDialog;
import edument.perl6idea.psi.Perl6RoutineDecl;

public class Perl6InlineRoutineDialog extends InlineOptionsDialog {
    private final Perl6RoutineDecl myRoutineDecl;
    private final PsiElement myReference;
    private final Editor myEditor;
    //private final boolean myAllowInlineThisOnly;
    private int myOccurrencesNumber;

    protected Perl6InlineRoutineDialog(Project project,
                                       Perl6RoutineDecl routine,
                                       PsiElement call,
                                       Editor editor, boolean allowInlineThisOnly) {
        super(project, true, routine);
        myRoutineDecl = routine;
        myReference = call;
        myEditor = editor;
        myInvokedOnReference = call != null;
        setTitle("Inline Routine");
        myOccurrencesNumber = getNumberOfOccurrences(routine);
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
        invokeRefactoring(
            new InlinePerl6RoutineProcessor(
                myProject, myRoutineDecl, myReference, myEditor,
                isInlineThisOnly(), !isKeepTheDeclaration())
        );
    }

    @Override
    public boolean isInlineThisOnly() {
        if (ApplicationManager.getApplication().isUnitTestMode()) return myReference != null;
        return super.isInlineThisOnly();
    }

    @Override
    public boolean isKeepTheDeclaration() {
        if (ApplicationManager.getApplication().isUnitTestMode()) return myReference != null;
        return super.isKeepTheDeclaration();
    }
}
