package edument.perl6idea.refactoring.inline.call;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.refactoring.inline.Perl6InlineDialog;

public class Perl6InlineCallDialog extends Perl6InlineDialog {
    private final Perl6RoutineDecl myRoutineDecl;
    private final PsiElement myReference;
    private final Editor myEditor;
    //private final boolean myAllowInlineThisOnly;

    protected Perl6InlineCallDialog(Project project,
                                    Perl6RoutineDecl routine,
                                    PsiElement call,
                                    Editor editor, boolean allowInlineThisOnly) {
        super(project, routine, call, editor);
        myRoutineDecl = routine;
        myReference = call;
        myEditor = editor;
        myInvokedOnReference = call != null;
        setTitle("Inline Routine");
        init();
    }

    @Override
    protected String getNameLabelText() {
        return String.format("Inline %s %s", myRoutineDecl.getRoutineKind(), myRoutineDecl.getRoutineName());
    }

    @Override
    protected void doAction() {
        invokeRefactoring(
            new Perl6InlineRoutineProcessor(
                myProject, myRoutineDecl, myReference, myEditor,
                isInlineThisOnly(), !isKeepTheDeclaration())
        );
    }
}
