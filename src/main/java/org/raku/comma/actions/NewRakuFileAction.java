package org.raku.comma.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public abstract class NewRakuFileAction<T extends DialogWrapper> extends AnAction {
    @Override
    public void update(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        final Presentation presentation = e.getPresentation();
        final boolean enabled = isAvailable(dataContext);
        presentation.setEnabledAndVisible(enabled);
    }

    private static boolean isAvailable(DataContext dataContext) {
        final Project project = CommonDataKeys.PROJECT.getData(dataContext);
        final Object navigatable = CommonDataKeys.NAVIGATABLE.getData(dataContext);
        return project != null && (navigatable instanceof PsiDirectory ||
                                   navigatable instanceof PsiFile);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) return;

        Object navigatable = e.getData(CommonDataKeys.NAVIGATABLE);
        String filePath = null;
        if (navigatable != null) {
            if (navigatable instanceof PsiDirectory)
                filePath = ((PsiDirectory) navigatable).getVirtualFile().getPath();
            else if (navigatable instanceof PsiFile) {
                PsiDirectory parent = ((PsiFile)navigatable).getParent();
                if (parent != null)
                    filePath = parent.getVirtualFile().getPath();
            }
        }
        if (filePath == null)
            return;

        T dialog = getDialog(project, filePath);
        boolean isOk = dialog.showAndGet();
        // User cancelled action
        if (!isOk) return;

        processDialogResult(project, filePath, dialog);
    }

    protected abstract void processDialogResult(Project project, String path, T dialog);

    @NotNull
    abstract protected T getDialog(Project project, String filePath);


    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}
