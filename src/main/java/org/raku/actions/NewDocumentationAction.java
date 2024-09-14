package org.raku.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.utils.RakuUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NewDocumentationAction extends NewRakuFileAction<NewDocumentationDialog> {
    @Override
    protected void processDialogResult(Project project, String docPath, NewDocumentationDialog dialog) {
        String fileName = dialog.getFileName();
        // If user cancelled action.
        if (fileName == null)
            return;

        docPath = stubDoc(Paths.get(docPath), fileName);
        VirtualFile docFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(docPath);
        assert docFile != null;
        FileEditorManager.getInstance(project).openFile(docFile, true);
    }

    private static String stubDoc(Path path, String name) {
        Path docPath = path.resolve(name);
        // If no extension, add default `.rakudoc`
        if (!name.contains("."))
            docPath = Paths.get(path.toString(), name + ".rakudoc");
        List<String> lines = new ArrayList<>();
        RakuUtils.writeCodeToPath(docPath, lines);
        return docPath.toString();
    }

    @NotNull
    @Override
    protected NewDocumentationDialog getDialog(Project project, String filePath) {
        return new NewDocumentationDialog(project, filePath);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
