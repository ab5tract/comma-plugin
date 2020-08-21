package edument.perl6idea.actions;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.module.builder.Perl6ModuleBuilderModule;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) return;

        Object navigatable = e.getData(CommonDataKeys.NAVIGATABLE);
        PsiDirectory psiDirectory = null;
        if (navigatable instanceof PsiDirectory) {
            psiDirectory = (PsiDirectory)navigatable;
        }
        else if (navigatable instanceof PsiFile) {
            psiDirectory = ((PsiFile)navigatable).getParent();
        }
        if (psiDirectory == null)
            throw new IllegalStateException(
                "Cannot create a module in this location, try an ordinary IDEA module directory or a file inside of desired directory");

        Module module = ModuleUtilCore.findModuleForFile(psiDirectory.getVirtualFile(), project);
        if (module == null)
            throw new IllegalStateException("Cannot create a module in this IDEA module");
        String modulePrefix = processNavigatable(module, psiDirectory);
        Perl6MetaDataComponent metaData = module.getService(Perl6MetaDataComponent.class);

        NewModuleDialog dialog = new NewModuleDialog(project, psiDirectory.getVirtualFile().getPath(), modulePrefix);
        boolean isOk = dialog.showAndGet();
        // User cancelled action
        if (!isOk) return;
        String moduleName = dialog.getModuleName();
        String moduleType = dialog.getModuleType();
        boolean isUnitScoped = dialog.isUnitModule();
        String modulePath = Perl6ModuleBuilderModule.stubModule(
            metaData, Paths.get(myBaseDir), moduleName, !metaData.isMetaDataExist(),
            true, null, moduleType, isUnitScoped);
        VirtualFile moduleFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(Paths.get(modulePath).toFile());
        if (moduleFile != null) {
            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, moduleFile);
            descriptor.navigate(true);
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor != null) {
                int textLength = editor.getDocument().getTextLength();
                int offset = isUnitScoped ?
                             textLength :    // In unit scoped we don't need to indent
                             textLength - 3; // Magic 3 here is: `\n`, `}` and `\n` again to get into newly created block
                // For `empty` case we will get `-3` here, so reset it
                if (offset < 0) offset = 0;
                CaretModel caretModel = editor.getCaretModel();
                caretModel.moveToOffset(offset);

                CommandProcessor.getInstance().executeCommand(project, () -> {
                    EditorActionManager actionManager = EditorActionManager.getInstance();
                    EditorActionHandler actionHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_MOVE_LINE_END);
                    try {
                        actionHandler.execute(editor, caretModel.getCurrentCaret(), DataManager.getInstance().getDataContextFromFocusAsync().blockingGet(2000));
                    }
                    catch (ExecutionException | TimeoutException ex) {
                        LOG.warn(ex);
                    }
                }, "", null);
            }
        }
        else
            LOG.warn("File was not created");
    }

    public String processNavigatable(Module module, PsiDirectory psiDirectory) {
        VirtualFile sourceRoot = ProjectFileIndex.getInstance(module.getProject()).getSourceRootForFile(psiDirectory.getVirtualFile());
        if (sourceRoot == null) { // It might be a location outside of source roots, so just set it
            myBaseDir = psiDirectory.getVirtualFile().getPath();
        } else {
            myBaseDir = sourceRoot.getPath();
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
