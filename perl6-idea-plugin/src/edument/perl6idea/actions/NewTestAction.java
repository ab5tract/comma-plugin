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
import edument.perl6idea.module.builder.Perl6ModuleBuilderModule;
import edument.perl6idea.utils.Patterns;

import java.nio.file.Paths;
import java.util.Collections;

public class NewTestAction extends AnAction {
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
                return inputString.matches(Patterns.TEST_PATTERN);
            }

            @Override
            public boolean canClose(String inputString) {
                return inputString.matches(Patterns.TEST_PATTERN);
            }
        };

        Object navigatable = e.getData(CommonDataKeys.NAVIGATABLE);
        String testPath = null;
        if (navigatable != null) {
            if (navigatable instanceof PsiDirectory)
                testPath = ((PsiDirectory) navigatable).getVirtualFile().getPath();
            else if (navigatable instanceof PsiFile)
                if (((PsiFile) navigatable).getParent() != null)
                    testPath = ((PsiFile) navigatable).getParent().getVirtualFile().getPath();
        }

        String fileName = Messages.showInputDialog(
            project,
            "Test file name (type one without an extension to use a default '.t'):",
            "New Test Name",
            Messages.getQuestionIcon(), null, validator);
        if (fileName == null)
            return;

        if (testPath == null) {
            VirtualFile path = project.getBaseDir();
            if (path == null) return;
            testPath = Paths.get(path.getPath(), "t").toString();
        }

        testPath = Perl6ModuleBuilderModule.stubTest(Paths.get(testPath), fileName, Collections.emptyList());
        VirtualFile testFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(testPath);
        assert testFile != null;
        FileEditorManager.getInstance(project).openFile(testFile, true);

    }
}
