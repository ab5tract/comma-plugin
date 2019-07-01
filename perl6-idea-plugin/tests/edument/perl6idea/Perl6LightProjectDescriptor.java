package edument.perl6idea;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class Perl6LightProjectDescriptor extends LightProjectDescriptor {
    @Nullable
    @Override
    public VirtualFile createSourcesRoot(@NotNull Module module) {
        VirtualFile projectRoot = module.getProject().getBaseDir();
        assert projectRoot != null;
        projectRoot.refresh(false, true);
        VirtualFile srcRoot = doCreateSourceRoot(projectRoot, "lib");
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return srcRoot;
    }

    private void cleanSourceRoot(VirtualFile contentRoot) throws IOException {
        LocalFileSystem vfs = (LocalFileSystem) contentRoot.getFileSystem();
        for (VirtualFile child : contentRoot.getChildren()) {
            if (vfs.exists(child))
                child.delete(this);
        }
    }
}
