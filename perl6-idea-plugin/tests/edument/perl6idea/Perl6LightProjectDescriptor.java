package edument.perl6idea;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6LightProjectDescriptor extends LightProjectDescriptor {
    @Nullable
    @Override
    public VirtualFile createSourcesRoot(@NotNull Module module) {
        return super.createSourceRoot(module, "lib");
    }
}
