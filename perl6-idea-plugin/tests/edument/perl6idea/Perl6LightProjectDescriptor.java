package edument.perl6idea;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.RootModelProvider;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class Perl6LightProjectDescriptor extends LightProjectDescriptor {
    @Nullable
    @Override
    public VirtualFile createSourcesRoot(@NotNull Module module) {
        ContentEntry[] entries = ModuleRootManager.getInstance(module).getContentEntries();
        if (entries.length == 0) {
            ModifiableRootModel rootModel = ModuleRootManager.getInstance(module).getModifiableModel();
            String stringPath = ModuleUtilCore.getModuleDirPath(module);
            ContentEntry entry = rootModel.addContentEntry(Paths.get(stringPath).toUri().toString());
            entries = new ContentEntry[]{entry};
            rootModel.commit();
        }
        else if (entries.length != 1) {
            assert false;
        }
        VirtualFile moduleRoot = entries[0].getFile();
        assert moduleRoot != null;
        moduleRoot.refresh(false, true);
        VirtualFile srcRoot = doCreateSourceRoot(moduleRoot, "lib");
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
