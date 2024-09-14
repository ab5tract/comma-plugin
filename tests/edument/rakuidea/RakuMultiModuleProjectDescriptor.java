package edument.rakuidea;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

public class RakuMultiModuleProjectDescriptor extends RakuLightProjectDescriptor {
    @Override
    public void setUpProject(@NotNull Project project, @NotNull SetupHandler handler) {
        super.setUpProject(project, handler);
        WriteAction.run(() -> {
            createRakuModule(
                project, handler, "Module::Inner",
                FileUtil.join(FileUtil.getTempDirectory(), "Inner", TEST_MODULE_NAME + "_inner.iml"));
            Module[] modules = ModuleManager.getInstance(project).getModules();
            ModuleRootModificationUtil.addDependency(modules[0], modules[1]);
        });
    }
}
