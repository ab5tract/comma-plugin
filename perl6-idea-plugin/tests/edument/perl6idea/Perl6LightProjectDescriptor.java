package edument.perl6idea;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class Perl6LightProjectDescriptor extends LightProjectDescriptor {
    @Override
    public void setUpProject(@NotNull Project project, @NotNull SetupHandler handler) {
        WriteAction.run(() -> {
            Module module = createMainModule(project);
            ModuleRootModificationUtil.updateModel(module, (model) -> {
                model.addContentEntry(Paths.get(module.getModuleFilePath()).getParent().toUri().toString());
            });
            handler.moduleCreated(module);
            VirtualFile sourceRoot = createSourcesRoot(module);
            if (sourceRoot != null) {
                handler.sourceRootCreated(sourceRoot);
            }
        });
    }

    @Nullable
    @Override
    public VirtualFile createSourcesRoot(@NotNull Module module) {
        VirtualFile moduleRoot = ModuleRootManager.getInstance(module).getContentEntries()[0].getFile();
        assert moduleRoot != null;
        moduleRoot.refresh(false, true);
        VirtualFile srcRoot = doCreateSourceRoot(moduleRoot, "lib");
        ModuleRootModificationUtil.updateModel(module, model -> {
            ContentEntry[] entries = model.getContentEntries();
            entries[0].addSourceFolder(srcRoot, false);
        });
        registerSourceRoot(module.getProject(), srcRoot);
        return srcRoot;
    }

    @Override
    protected VirtualFile doCreateSourceRoot(VirtualFile root, String srcPath) {
        VirtualFile srcRoot;
        try {
            VirtualFile child = root.findChild(srcPath);
            if (child != null)
                child.delete(this);
            srcRoot = root.createChildDirectory(this, srcPath);
            cleanSourceRoot(srcRoot);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return srcRoot;
    }

    private void cleanSourceRoot(VirtualFile contentRoot) throws IOException {
        LocalFileSystem vfs = (LocalFileSystem)contentRoot.getFileSystem();
        for (VirtualFile child : contentRoot.getChildren()) {
            if (vfs.exists(child))
                child.delete(this);
        }
    }
}
