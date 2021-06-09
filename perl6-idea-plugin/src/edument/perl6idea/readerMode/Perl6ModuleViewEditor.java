package edument.perl6idea.readerMode;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.util.Objects;

public class Perl6ModuleViewEditor extends UserDataHolderBase implements FileEditor {
    private final TextEditor myEditor;
    private final String myName;
    private PodPreviewEditor myViewer;
    private JComponent myComponent;
    private Runnable myTriggerPodRenderCode;

    public Perl6ModuleViewEditor(TextEditor editor, PodPreviewEditor viewer, String editorName) {
        myEditor = editor;
        myViewer = viewer;
        myName = editorName;
    }

    @Override
    public @NotNull JComponent getComponent() {
        if (myComponent == null) {
            myComponent = new JPanel(new MigLayout("fill, insets 0"));
            myComponent.add(myEditor.getComponent(), "hidemode 3, grow, width 50:50");
            myComponent.add(myViewer.getComponent(), "hidemode 3, grow, width 50:50");
            myEditor.getComponent().setVisible(true);
            myViewer.getComponent().setVisible(false);
        }
        return myComponent;
    }

    public void updateState(Perl6ReaderModeState state) {
        ApplicationManager.getApplication().invokeLater(() -> {
            PsiFile psiFile = PsiDocumentManager.getInstance(Objects.requireNonNull(myEditor.getEditor().getProject()))
                .getPsiFile(myEditor.getEditor().getDocument());
            if (psiFile != null) {
                psiFile.putUserData(Perl6ActionProvider.RAKU_EDITOR_MODE_STATE, state);
            }
            myTriggerPodRenderCode.run();
            if (state == Perl6ReaderModeState.DOCS) {
                myEditor.getComponent().setVisible(false);
                myViewer.getComponent().setVisible(true);
            }
            else if (state == Perl6ReaderModeState.CODE) {
                myEditor.getComponent().setVisible(true);
                myViewer.getComponent().setVisible(false);
            }
            else if (state == Perl6ReaderModeState.SPLIT) {
                myEditor.getComponent().setVisible(true);
                myViewer.getComponent().setVisible(true);
            }
        });
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return myComponent;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return myName;
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
        if (state instanceof Perl6ModuleEditorState) {
            Perl6ModuleEditorState compositeState = (Perl6ModuleEditorState)state;
            if (compositeState.getEditorState() != null) {
                myEditor.setState(compositeState.getEditorState());
            }
            if (compositeState.getViewerState() != null) {
                myViewer.setState(compositeState.getViewerState());
            }
        }
    }

    @Override
    public @NotNull FileEditorState getState(@NotNull FileEditorStateLevel level) {
        return new Perl6ModuleEditorState(myEditor.getState(level), myViewer.getState(level));
    }

    @Override
    public boolean isModified() {
        return myEditor.isModified();
    }

    @Override
    public boolean isValid() {
        return myEditor.isValid();
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
        myEditor.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
        myEditor.removePropertyChangeListener(listener);
    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return myEditor.getCurrentLocation();
    }

    @Override
    public void dispose() {
        Disposer.dispose(myEditor);
    }

    public void setViewer(PodPreviewEditor viewer) {
        myViewer = viewer;
    }

    public void setCallback(Runnable triggerPodRenderCode) {
        myTriggerPodRenderCode = triggerPodRenderCode;
    }

    private static class Perl6ModuleEditorState implements FileEditorState {
        private final FileEditorState myEditorState;
        private final FileEditorState myViewerState;

        private Perl6ModuleEditorState(@NotNull FileEditorState editorState, FileEditorState viewerState) {
            myEditorState = editorState;
            myViewerState = viewerState;
        }

        @Override
        public boolean canBeMergedWith(FileEditorState otherState, FileEditorStateLevel level) {
            return otherState instanceof Perl6ModuleEditorState;
        }

        public FileEditorState getEditorState() {
            return myEditorState;
        }

        public FileEditorState getViewerState() {
            return myViewerState;
        }
    }
}
