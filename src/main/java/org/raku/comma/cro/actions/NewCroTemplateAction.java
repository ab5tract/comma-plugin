package org.raku.comma.cro.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.raku.comma.cro.template.CroTemplateFileType;
import org.raku.comma.utils.Patterns;
import org.raku.comma.utils.RakuUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class NewCroTemplateAction extends AnAction {
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
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) return;

        Object navigatable = e.getData(CommonDataKeys.NAVIGATABLE);
        String templatePath = null;
        if (navigatable != null) {
            if (navigatable instanceof PsiDirectory)
                templatePath = ((PsiDirectory) navigatable).getVirtualFile().getPath();
            else if (navigatable instanceof PsiFile) {
                PsiDirectory parent = ((PsiFile)navigatable).getParent();
                if (parent != null)
                    templatePath = parent.getVirtualFile().getPath();
            }
        }
        if (templatePath == null)
            return;

        String finalTemplatePath = templatePath;
        InputValidator validator = new InputValidator() {
            @Override
            public boolean checkInput(String inputString) {
                String string = inputString;
                if (string.indexOf('.') < 0)
                    string += ".crotmp";
                return !Paths.get(finalTemplatePath, string).toFile().exists() && inputString.matches(Patterns.CRO_TEMPLATE_PATTERN);
            }

            @Override
            public boolean canClose(String inputString) {
                return inputString.matches(Patterns.CRO_TEMPLATE_PATTERN);
            }
        };

        String fileName = Messages.showInputDialog(
                project,
                "Cro Template name (type one without an extension to use a default '.crotmp'):",
                "New Cro Template Name",
                Messages.getQuestionIcon(), null, validator);
        if (fileName == null)
            return;

        if (fileName.indexOf('.') < 0)
            fileName += ".crotmp";

        templatePath = stubTemplate(Paths.get(templatePath), fileName);
        VirtualFile testFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(templatePath);
        assert testFile != null;
        FileEditorManager.getInstance(project).openFile(testFile, true);
    }


    public static String stubTemplate(Path testDirectoryPath, String fileName) {
        Path testPath = testDirectoryPath.resolve(fileName);
        // If no extension, add default `.crotmp`
        if (!fileName.contains("."))
            testPath = Paths.get(testDirectoryPath.toString(), fileName + "." + CroTemplateFileType.INSTANCE.getDefaultExtension());
        RakuUtils.writeCodeToPath(testPath, new ArrayList<>());
        return testPath.toString();
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}
