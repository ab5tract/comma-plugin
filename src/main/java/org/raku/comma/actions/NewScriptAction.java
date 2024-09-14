package org.raku.comma.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.comma.language.RakuLanguageVersionService;
import org.raku.comma.module.builder.RakuModuleBuilderScript;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

public final class NewScriptAction extends NewRakuFileAction<NewScriptDialog> {
    @Override
    protected void processDialogResult(Project project, String scriptPath, NewScriptDialog dialog) {
        String fileName = dialog.getScriptName();
        boolean shouldFill = dialog.shouldAddTemplate();
        // If user cancelled action.
        if (fileName == null) return;

        RakuLanguageVersionService service = project.getService(RakuLanguageVersionService.class);
        scriptPath = RakuModuleBuilderScript.stubScript(
            Paths.get(scriptPath), fileName, shouldFill, service.getVersion());
        VirtualFile scriptFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(scriptPath);
        assert scriptFile != null;
        FileEditorManager.getInstance(project).openFile(scriptFile, true);
    }

    @Override
    protected @NotNull NewScriptDialog getDialog(Project project, String filePath) {
        return new NewScriptDialog(project, filePath);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
