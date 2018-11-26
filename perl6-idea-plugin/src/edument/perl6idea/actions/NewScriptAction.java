package edument.perl6idea.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import edument.perl6idea.module.Perl6ModuleBuilder;
import edument.perl6idea.utils.Patterns;

public class NewScriptAction extends AnAction {
    @Override
    public void update(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();
        final Presentation presentation = e.getPresentation();
        final boolean enabled = isAvailable(dataContext);
        presentation.setVisible(enabled);
        presentation.setEnabled(enabled);
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
        InputValidator validator = new InputValidator() {
            @Override
            public boolean checkInput(String inputString) {
                return inputString.matches(Patterns.SCRIPT_PATTERN);
            }

            @Override
            public boolean canClose(String inputString) {
                return inputString.matches(Patterns.SCRIPT_PATTERN);
            }
        };
        String fileName = Messages.showInputDialog(project,
                    "Script name (example: foo, foo.p6):",
                    "New Script Name",
                    Messages.getQuestionIcon(), null, validator);
        // If user cancelled action.
        if (fileName == null)
            return;

        Object navigatable = e.getData(CommonDataKeys.NAVIGATABLE);
        String scriptPath = null;
        if (navigatable != null) {
            if (navigatable instanceof PsiDirectory)
                scriptPath = ((PsiDirectory) navigatable).getVirtualFile().getPath();
            else if (navigatable instanceof PsiFile)
                if (((PsiFile) navigatable).getParent() != null)
                    scriptPath = ((PsiFile) navigatable).getParent().getVirtualFile().getPath();
        }

        scriptPath = Perl6ModuleBuilder.stubScript(
                scriptPath != null ? scriptPath :
                project.getBaseDir().getCanonicalPath(), fileName);
        VirtualFile scriptFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(scriptPath);
        assert scriptFile != null;
        FileEditorManager.getInstance(project).openFile(scriptFile, true);
    }
}
