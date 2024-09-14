package edument.rakuidea;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import edument.rakuidea.module.RakuModuleBuilder;
import edument.rakuidea.module.RakuModuleType;
import edument.rakuidea.module.RakuModuleWizardStep;
import edument.rakuidea.utils.RakuProjectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RakuLightProjectDescriptor extends LightProjectDescriptor {
    @Override
    public @NotNull String getModuleTypeId() {
        return RakuModuleType.ID;
    }

    @Override
    public void setUpProject(@NotNull Project project, @NotNull SetupHandler handler) {
        WriteAction.run(() -> createRakuModule(project, handler, "Module::Outer",
                                           FileUtil.join(FileUtil.getTempDirectory(), TEST_MODULE_NAME + ".iml")));
    }

    protected void createRakuModule(@NotNull Project project,
                                    @NotNull LightProjectDescriptor.@NotNull SetupHandler handler,
                                    String moduleName,
                                    String moduleFilePath) {
        RakuModuleBuilder builder = new RakuModuleBuilder();
        builder.setPerl6ModuleType(RakuProjectType.PERL6_MODULE);
        builder.setModuleFilePath(moduleFilePath);
        Map<String, String> data = new HashMap<>();
        data.put(RakuModuleWizardStep.MODULE_NAME, moduleName);
        builder.updateLocalBuilder(data);
        Module module = createModule(project, moduleFilePath);
        ModuleRootModificationUtil.updateModel(module, (model) -> builder.setupRootModel(model));
        handler.moduleCreated(module);
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
        for (VirtualFile root : roots) {
            if (root.getName().equals("lib"))
                handler.sourceRootCreated(root);
        }
    }
}
