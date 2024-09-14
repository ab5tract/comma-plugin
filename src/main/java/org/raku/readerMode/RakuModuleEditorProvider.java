package org.raku.readerMode;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.VisibleAreaEvent;
import com.intellij.openapi.editor.event.VisibleAreaListener;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.util.Alarm;
import org.raku.filetypes.RakuModuleFileType;
import org.raku.filetypes.RakuPodFileType;
import org.raku.psi.RakuFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@InternalIgnoreDependencyViolation
public class RakuModuleEditorProvider implements FileEditorProvider, DumbAware {
    private static final String EDITOR_TYPE_ID = "rakuidea-module";

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return file.getFileType() instanceof RakuModuleFileType || file.getFileType() instanceof RakuPodFileType;
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        TextEditor editor = (TextEditor)TextEditorProvider.getInstance().createEditor(project, file);
        if (JBCefApp.isSupported()) {
            RakuModuleViewEditor moduleViewEditor = new RakuModuleViewEditor(editor, null, file, "Raku Module Editor");
            PodPreviewEditor viewer = new PodPreviewEditor(project, file, (EditorImpl)editor.getEditor(), moduleViewEditor);
            Disposer.register(editor, viewer);

            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
            Alarm myAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, editor);

            moduleViewEditor.setCallback(
                () -> myAlarm.addRequest(() -> renderPreview(editor.getEditor().getDocument(), documentManager, viewer), 0));

            editor.getEditor().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    if (moduleViewEditor.getPresentedState() == RakuReaderModeState.CODE)
                        return;
                    myAlarm.cancelAllRequests();
                    myAlarm.addRequest(() -> renderPreview(event.getDocument(), documentManager, viewer), 500);
                }
            }, editor);
            editor.getEditor().getScrollingModel().addVisibleAreaListener(new VisibleAreaListener() {
                @Override
                public void visibleAreaChanged(@NotNull VisibleAreaEvent e) {
                    if (moduleViewEditor.getPresentedState() == RakuReaderModeState.CODE)
                        return;

                    Editor editor = e.getEditor();
                    Rectangle nowInView = e.getNewRectangle();
                    LogicalPosition position = editor.xyToLogicalPosition(nowInView.getLocation());
                    int line = position.line;
                    int offset = editor.getDocument().getLineStartOffset(line);
                    viewer.scrollTo(offset);
                }
            });
            myAlarm.addRequest(() -> renderPreview(editor.getEditor().getDocument(), documentManager, viewer), 0);

            moduleViewEditor.setViewer(viewer);
            return moduleViewEditor;
        }
        return editor;
    }

    private static void renderPreview(Document document, PsiDocumentManager documentManager, PodPreviewEditor viewer) {
        viewer.setPodHtml(ReadAction.compute(() -> {
            PsiFile psi = documentManager.getPsiFile(document);
            if (!(psi instanceof RakuFile))
                return "";
            return ((RakuFile)psi).renderPod();
        }));
    }

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return EDITOR_TYPE_ID;
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}
