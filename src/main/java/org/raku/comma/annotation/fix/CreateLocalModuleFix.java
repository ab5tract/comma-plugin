package org.raku.comma.annotation.fix;

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
import org.raku.comma.actions.NewModuleDialog;
import org.raku.comma.language.RakuLanguageVersionService;
import org.raku.comma.metadata.RakuMetaDataComponent;
import org.raku.comma.module.builder.RakuModuleBuilderModule;
import org.raku.comma.psi.RakuModuleName;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;

public class CreateLocalModuleFix implements IntentionAction {
    private final Module module;
    private final RakuModuleName moduleName;

    public CreateLocalModuleFix(Module module, RakuModuleName name) {
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

        NewModuleDialog dialog = new NewModuleDialog(project, moduleLibraryPath, moduleName.getText());
        boolean isOk = dialog.showAndGet();
        if (! isOk) return;

        RakuLanguageVersionService langVersionService = project.getService(RakuLanguageVersionService.class);
        String newModulePath = RakuModuleBuilderModule.stubModule(
            module.getService(RakuMetaDataComponent.class),
            Paths.get(moduleLibraryPath),
            dialog.getModuleName(), false, true,
            moduleLibraryRoot.getParent(), dialog.getModuleType(), false, langVersionService.getVersion());
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
