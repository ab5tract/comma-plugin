package edument.perl6idea.readerMode;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.actionSystem.impl.ActionButtonWithText;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.editor.markup.InspectionWidgetActionProvider;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.ui.JBUI;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6ActionProvider implements InspectionWidgetActionProvider {
    public static final Key<Perl6ReaderModeState> RAKU_EDITOR_MODE_STATE = new Key<>("perl6.module.view.state.key");

    @Nullable
    @Override
    public AnAction createAction(@NotNull Editor editor) {
        Project project = editor.getProject();
        if (project == null || project.isDefault()) {
            return null;
        }
        else {
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            if (psiFile != null && psiFile.getFileType() instanceof Perl6ModuleFileType)
                return new DefaultActionGroup(
                    new CodeModeAction(editor), Separator.create(),
                    new DocsModeAction(editor), Separator.create(),
                    new SplitModeAction(editor), Separator.create()
                );
        }
        return null;
    }

    private abstract static class RotatedStateAction extends DumbAwareAction implements CustomComponentAction {
        final protected Perl6ReaderModeState myState;
        final protected Editor myEditor;

        RotatedStateAction(Perl6ReaderModeState state, Editor editor) {
            myState = state;
            myEditor = editor;
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            if (myEditor.getProject() == null)
                return;
            Presentation presentation = e.getPresentation();
            PsiFile file = PsiDocumentManager.getInstance(myEditor.getProject()).getPsiFile(myEditor.getDocument());
            if (file == null)
                return;
            Perl6ReaderModeState currentState = file.getUserData(RAKU_EDITOR_MODE_STATE);
            presentation.setEnabledAndVisible(currentState != Perl6ReaderModeState.SPLIT &&
                                              (currentState == null && myState != Perl6ReaderModeState.CODE ||
                                              currentState != null && currentState != myState));
            presentation.setText(myState.toString());
            String descriptionText;
            switch (myState) {
                case CODE:
                    descriptionText = "Display default editor view presenting Raku source code";
                    break;
                case DOCS:
                    descriptionText = "Display special editor view presenting rendered module documentation";
                    break;
                case SPLIT:
                    descriptionText = "Display special editor view presenting both Raku source code and rendered module documentation";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + myState);
            }
            presentation.setDescription(descriptionText);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Project project = e.getProject();

            if (project != null && myEditor instanceof EditorImpl) {
                for (FileEditor fileEditor : FileEditorManager.getInstance(project).getEditors(((EditorImpl)myEditor).getVirtualFile())) {
                    if (fileEditor instanceof Perl6ModuleViewEditor) {
                        ((Perl6ModuleViewEditor)fileEditor).updateState(myState);
                    }
                }
            }
        }

        @Override
        public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
            return new ActionButtonWithText(this, presentation, place, JBUI.size(18)) {
                @Override
                protected int iconTextSpace() {
                    return JBUI.scale(2);
                }
            };
        }
    }

    private static class CodeModeAction extends RotatedStateAction {
        CodeModeAction(@NotNull Editor editor) {
            super(Perl6ReaderModeState.CODE, editor);
        }
    }

    private static class DocsModeAction extends RotatedStateAction {
        DocsModeAction(@NotNull Editor editor) {
            super(Perl6ReaderModeState.DOCS, editor);
        }
    }

    private static class SplitModeAction extends RotatedStateAction {
        SplitModeAction(@NotNull Editor editor) {
            super(Perl6ReaderModeState.SPLIT, editor);
        }
    }
}
