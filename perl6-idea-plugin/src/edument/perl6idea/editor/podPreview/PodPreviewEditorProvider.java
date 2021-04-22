package edument.perl6idea.editor.podPreview;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.util.Alarm;
import edument.perl6idea.filetypes.Perl6PodFileType;
import edument.perl6idea.psi.Perl6File;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class PodPreviewEditorProvider implements FileEditorProvider, DumbAware {
    @NonNls private static final String EDITOR_TYPE_ID = "pod6";

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return EDITOR_TYPE_ID;
    }

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return file.getFileType() instanceof Perl6PodFileType;
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        TextEditor editor = (TextEditor)TextEditorProvider.getInstance().createEditor(project, file);
        if (JBCefApp.isSupported()) {
            PodPreviewEditor viewer = new PodPreviewEditor();
            Alarm myAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, editor);
            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
            editor.getEditor().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    myAlarm.cancelAllRequests();
                    myAlarm.addRequest(() -> renderPreview(event.getDocument(), documentManager, viewer), 500);
                }
            }, editor);
            myAlarm.addRequest(() -> renderPreview(editor.getEditor().getDocument(), documentManager, viewer), 0);
            return new TextEditorWithPreview(editor, viewer, "Pod6Editor");
        }
        else {
            return editor;
        }
    }

    private static void renderPreview(Document document, PsiDocumentManager documentManager, PodPreviewEditor viewer) {
        viewer.setPodHtml(ReadAction.compute(() -> {
            PsiFile psi = documentManager.getPsiFile(document);
            if (!(psi instanceof Perl6File))
                return "";
            return ((Perl6File)psi).renderPod();
        }));
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}
