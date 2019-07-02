package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.actions.NewModuleDialog;
import edument.perl6idea.metadata.Perl6MetaDataComponent;
import edument.perl6idea.module.builder.Perl6ModuleBuilderModule;
import edument.perl6idea.psi.Perl6ModuleName;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

public class CreateLocalModuleFix implements IntentionAction {
    private final Module module;
    private final Perl6ModuleName moduleName;

    public CreateLocalModuleFix(Module module, Perl6ModuleName name) {
        this.module = module;
        this.moduleName = name;
    }

    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Stub a local module";
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        VirtualFile moduleLibraryRoot = null;
        String moduleLibraryPath = null;
        ModifiableRootModel modifiableModel = ModuleRootManager.getInstance(module).getModifiableModel();
        for (VirtualFile root : modifiableModel.getSourceRoots()) {
            if (root.getName().equals("lib")) {
                moduleLibraryRoot = root;
                moduleLibraryPath = root.getCanonicalPath();
            }
        }
        modifiableModel.dispose();
        if (moduleLibraryPath == null)
            throw new IncorrectOperationException();

        NewModuleDialog dialog = new NewModuleDialog(project, false, moduleName.getText());
        boolean isOk = dialog.showAndGet();
        if (!isOk) return;

        String newModulePath = Perl6ModuleBuilderModule.stubModule(
            module.getComponent(Perl6MetaDataComponent.class),
            Paths.get(moduleLibraryPath),
            dialog.getModuleName(), false, true,
            moduleLibraryRoot.getParent(), dialog.getModuleType(), false);
        VirtualFile moduleFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(Paths.get(newModulePath).toFile());

        // If the user changed stubbed module name in form,
        // we update its usage too
        if (!dialog.getModuleName().equals(moduleName.getText())) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                moduleName.setName(dialog.getModuleName());
            });
        }

        if (moduleFile != null) {
            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, moduleFile);
            descriptor.navigate(true);
        }

    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
