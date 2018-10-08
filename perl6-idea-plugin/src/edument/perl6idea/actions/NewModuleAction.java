package edument.perl6idea.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import edument.perl6idea.module.Perl6ModuleBuilder;
import edument.perl6idea.utils.Patterns;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NewModuleAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(NewModuleAction.class);
    private String myBaseDir;

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) return;
        InputValidator validator = new InputValidator() {
            @Override
            public boolean checkInput(String inputString) {
                return inputString.matches(Patterns.MODULE_PATTERN);
            }

            @Override
            public boolean canClose(String inputString) {
                return inputString.matches(Patterns.MODULE_PATTERN);
            }
        };

        myBaseDir = project.getBaseDir().getCanonicalPath();
        if (myBaseDir == null)
            throw new IllegalStateException("Cannot create a module without a project base directory!");
        myBaseDir = Paths.get(myBaseDir, "lib").toString();

        Object navigatable = e.getData(CommonDataKeys.NAVIGATABLE);
        String modulePrefix = null;
        if (navigatable != null) {
            PsiDirectory psiDirectory = null;
            if (navigatable instanceof PsiDirectory) {
                psiDirectory = (PsiDirectory) navigatable;
            } else if (navigatable instanceof PsiFile) {
                psiDirectory = ((PsiFile) navigatable).getParent();
            }
            modulePrefix = processNavigatable(psiDirectory);
        }

        String moduleName = Messages.showInputDialog(project,
                "Module name (examples: TopLevelName, My::Module):",
                "New Module Name",
                Messages.getQuestionIcon(), modulePrefix, validator);
        // If user cancelled action.
        if (moduleName == null) return;
        Path meta = Perl6ModuleBuilder.getMETAFilePath(project);
        boolean metaExists = meta != null && Files.exists(meta);

        String modulePath = Perl6ModuleBuilder.stubModule(project, myBaseDir,
                                                          moduleName, null,
                                                          !metaExists);
        VirtualFile moduleFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(Paths.get(modulePath).toFile());
        if (moduleFile != null)
            FileEditorManager.getInstance(project).openFile(moduleFile, true);
        else
            LOG.warn("File was not created");
    }

    public String processNavigatable(PsiDirectory psiDirectory) {
        if (psiDirectory != null) {
            String path = psiDirectory.getVirtualFile().getPath();
            // On windows, myBaseDir has canonical path, while `path` is not OS-indenendent
            // It does not work if call `getCanonicalPath` for psiDirectory
            String tempBaseDir = myBaseDir;
            if (SystemInfo.isWindows) {
                tempBaseDir = tempBaseDir.replaceAll("\\\\", "/") + "/";
            } else {
                tempBaseDir += "/";
            }
            if (path.startsWith(tempBaseDir)) {
                // Get full path, cut off its prefix up to "lib" directory, split it by `\` or `/` symbols,
                // then join pieces with `::` as delimiter
                String cuttedPath = String.join("::", path.substring(tempBaseDir.length()).split("[/\\\\]"));
                // if it's linux, we have `/` as first symbol, so because of previous line,
                // we have `::Foo` now. In this case, trim it
                // and add `::` postfix, so user has `Foo::`, not `Foo` pre-filled
                return StringUtil.trimStart(cuttedPath, "::") + "::";
            } else {
                myBaseDir = path;
            }
        }
        return null;
    }

    public String getBaseDir() {
        return myBaseDir;
    }

    public void setBaseDir(String baseDir) {
        myBaseDir = baseDir;
    }
}
