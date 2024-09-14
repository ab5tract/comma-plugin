package org.raku.comma.refactoring.inline.call;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.raku.comma.psi.RakuRoutineDecl;
import org.raku.comma.refactoring.inline.RakuInlineDialog;

public class RakuInlineCallDialog extends RakuInlineDialog {
    private final RakuRoutineDecl myRoutineDecl;
    private final PsiElement myReference;
    private final Editor myEditor;

    protected RakuInlineCallDialog(Project project,
                                   RakuRoutineDecl routine,
                                   PsiElement call,
                                   Editor editor) {
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
            new RakuInlineRoutineProcessor(
                myProject, myRoutineDecl, myReference, myEditor,
                isInlineThisOnly(), !isKeepTheDeclaration())
        );
    }
}
