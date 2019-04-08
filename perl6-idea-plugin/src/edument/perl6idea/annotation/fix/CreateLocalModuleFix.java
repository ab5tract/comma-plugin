package edument.perl6idea.annotation.fix;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.ide.util.projectWizard.ModuleBuilderFactory;
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
import edument.perl6idea.module.Perl6MetaDataComponent;
import edument.perl6idea.module.Perl6ModuleBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

public class CreateLocalModuleFix implements IntentionAction {
    private final Module module;
    private final String moduleName;

    public CreateLocalModuleFix(Module module, String name) {
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


        String newModulePath = Perl6ModuleBuilder.stubModule(
            module.getComponent(Perl6MetaDataComponent.class),
            moduleLibraryPath,
            moduleName, false, true,
            moduleLibraryRoot.getParent(), "", false);
        VirtualFile moduleFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(Paths.get(newModulePath).toFile());
        if (moduleFile != null) {
            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, moduleFile);
            descriptor.navigate(true);
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}