package edument.perl6idea.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import edument.perl6idea.module.Perl6MetaDataComponent;
import edument.perl6idea.module.Perl6ModuleBuilder;
import edument.perl6idea.utils.Patterns;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewModuleAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(NewModuleAction.class);
    private String myBaseDir;

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
        Project project = e.getProject();
        assert project != null;

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

        Object navigatable = e.getData(CommonDataKeys.NAVIGATABLE);
        PsiDirectory psiDirectory = null;
        if (navigatable instanceof PsiDirectory) {
            psiDirectory = (PsiDirectory) navigatable;
        } else if (navigatable instanceof PsiFile) {
            psiDirectory = ((PsiFile) navigatable).getParent();
        }
        if (psiDirectory == null)
            throw new IllegalStateException("Cannot create a module in this location, try an ordinary IDEA module directory or a file inside of desired directory");

        Module module = ModuleUtilCore.findModuleForFile(psiDirectory.getVirtualFile(), project);


        if (module == null)
            throw new IllegalStateException("Cannot create a module in this IDEA module");
        String modulePrefix = processNavigatable(module, psiDirectory);

        String moduleName = Messages.showInputDialog(project,
                                                     "Module name (examples: TopLevelName, My::Module):",
                                                     "New Module Name",
                                                     Messages.getQuestionIcon(), modulePrefix, validator);
        // If user cancelled action.
        if (moduleName == null) return;

        Perl6MetaDataComponent metaData = module.getComponent(Perl6MetaDataComponent.class);
        try {
            String modulePath = Perl6ModuleBuilder.stubModule(metaData, myBaseDir,
                                                              moduleName, !metaData.isMetaDataExist(), true, null);
            VirtualFile moduleFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(Paths.get(modulePath).toFile());
            if (moduleFile != null)
                FileEditorManager.getInstance(project).openFile(moduleFile, true);
            else
                LOG.warn("File was not created");
        }
        catch (IOException ex) {
            LOG.warn("File was not created");
        }
    }

    public String processNavigatable(Module module, PsiDirectory psiDirectory) {
        if (myBaseDir == null) {
            VirtualFile sourceRoot = ProjectFileIndex.getInstance(module.getProject()).getSourceRootForFile(psiDirectory.getVirtualFile());
            if (sourceRoot == null) { // It might be a location outside of source roots, so just set it
                myBaseDir = psiDirectory.getVirtualFile().getPath();
            } else {
                myBaseDir = sourceRoot.getPath();
            }
        }
        List<String> parts = new ArrayList<>();
        while (true) {
            if (psiDirectory != null &&
                Paths.get(psiDirectory.getVirtualFile().getPath()).startsWith(Paths.get(myBaseDir))) {
                parts.add(psiDirectory.getName());
                psiDirectory = psiDirectory.getParentDirectory();
            } else {
                break;
            }
        }
        Collections.reverse(parts);
        String prefix = String.join("::", parts.subList(1, parts.size()));
        if (!prefix.isEmpty())
            prefix += "::";
        return prefix;
    }

    public String getBaseDir() {
        return myBaseDir;
    }

    public void setBaseDir(String baseDir) {
        myBaseDir = baseDir;
    }
}
