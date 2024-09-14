package org.raku.comma.readerMode;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.actionSystem.impl.ActionButtonWithText;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.editor.markup.InspectionWidgetActionProvider;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.ui.JBUI;
import org.raku.comma.filetypes.RakuModuleFileType;
import org.raku.comma.filetypes.RakuPodFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@InternalIgnoreDependencyViolation
public class RakuActionProvider implements InspectionWidgetActionProvider {
    public static final Key<RakuReaderModeState> RAKU_EDITOR_MODE_STATE = new Key<>("raku.module.view.state.key");

    @Nullable
    @Override
    public AnAction createAction(@NotNull Editor editor) {
        Project project = editor.getProject();
        if (project == null || project.isDefault()) {
            return null;
        }
        else {
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            if (psiFile != null) {
                FileType type = psiFile.getFileType();
                if (type instanceof RakuModuleFileType || type instanceof RakuPodFileType) {
                    return new DefaultActionGroup(
                        new CodeModeAction(editor), Separator.create(),
                        new DocsModeAction(editor), Separator.create(),
                        new SplitModeAction(editor), Separator.create()
                    );
                }
            }
        }
        return null;
    }

    private abstract static class RotatedStateAction extends DumbAwareAction implements CustomComponentAction {
        final protected RakuReaderModeState myState;
        final protected Editor myEditor;

        RotatedStateAction(RakuReaderModeState state, Editor editor) {
            myState = state;
            myEditor = editor;
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            if (myEditor.getProject() == null) return;
            Presentation presentation = e.getPresentation();
            PsiFile file = PsiDocumentManager.getInstance(myEditor.getProject()).getPsiFile(myEditor.getDocument());
            if (file == null) return;
            RakuReaderModeState currentState = file.getUserData(RAKU_EDITOR_MODE_STATE);
            presentation.setEnabledAndVisible(currentState != RakuReaderModeState.SPLIT &&
                                              (currentState == null && myState != RakuReaderModeState.CODE ||
                                              currentState != null && currentState != myState));
            presentation.setText(myState.toString());
            String descriptionText = switch (myState) {
                case CODE -> "Display default editor view presenting Raku source code";
                case DOCS -> "Display special editor view presenting rendered module documentation";
                case SPLIT -> "Display special editor view presenting both Raku source code and rendered module documentation";
            };
            presentation.setDescription(descriptionText);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            Project project = e.getProject();

            if (project != null && myEditor instanceof EditorImpl) {
                for (FileEditor fileEditor : FileEditorManager.getInstance(project).getEditors(((EditorImpl)myEditor).getVirtualFile())) {
                    if (fileEditor instanceof RakuModuleViewEditor) {
                        ((RakuModuleViewEditor)fileEditor).updateState(myState);
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
            super(RakuReaderModeState.CODE, editor);
        }
        @Override public @NotNull ActionUpdateThread getActionUpdateThread() { return ActionUpdateThread.EDT; }
    }

    private static class DocsModeAction extends RotatedStateAction {
        DocsModeAction(@NotNull Editor editor) {
            super(RakuReaderModeState.DOCS, editor);
        }
        @Override public @NotNull ActionUpdateThread getActionUpdateThread() { return ActionUpdateThread.EDT; }
    }

    private static class SplitModeAction extends RotatedStateAction {
        SplitModeAction(@NotNull Editor editor) {
            super(RakuReaderModeState.SPLIT, editor);
        }
        @Override public @NotNull ActionUpdateThread getActionUpdateThread() { return ActionUpdateThread.EDT; }
    }
}
