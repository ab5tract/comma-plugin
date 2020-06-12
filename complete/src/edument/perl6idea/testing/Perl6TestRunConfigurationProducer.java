package edument.perl6idea.testing;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import edument.perl6idea.psi.Perl6File;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class Perl6TestRunConfigurationProducer extends LazyRunConfigurationProducer<Perl6TestRunConfiguration> {
    @NotNull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return Perl6CompleteTestConfigurationType.getInstance().getFactory();
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull Perl6TestRunConfiguration configuration,
                                                    @NotNull ConfigurationContext context,
                                                    @NotNull Ref<PsiElement> sourceElement) {
        final Location location = context.getLocation();
        if (location == null) return false;
        AtomicReference<String> pathHolder = new AtomicReference<>();
        if (location.getPsiElement().getContainingFile() != null) {
            final PsiFile file = location.getPsiElement().getContainingFile();
            if (!(file instanceof Perl6File)) return false;
            final VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null) return false;
            if (virtualFile instanceof LightVirtualFile) return false;
            configuration.setTestKind(RakuTestKind.FILE);
            configuration.setFilePath(virtualFile.getPath());
        }
        else {
            VirtualFile directory = location.getVirtualFile();
            if (directory == null || !directory.isDirectory()) {
                return false;
            }
            VfsUtilCore.processFilesRecursively(directory, file -> {
                if (Arrays.asList("t", "t6", "rakutest").contains(file.getExtension())) {
                    pathHolder.set(directory.getPath());
                    return false;
                }
                return true;
            });
            String path = pathHolder.get();
            if (path == null)
                return false;
            configuration.setTestKind(RakuTestKind.DIRECTORY);
            configuration.setDirectoryPath(path);
        }
        configuration.setInterpreterParameters("");
        configuration.setName(configuration.suggestedName());
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull Perl6TestRunConfiguration configuration, @NotNull ConfigurationContext context) {
        if (configuration.getTestKind() != RakuTestKind.FILE && configuration.getTestKind() != RakuTestKind.DIRECTORY) {
            return false;
        }

        final Location location = context.getLocation();
        if (location == null) return false;
        AtomicReference<String> pathHolder = new AtomicReference<>();
        if (location.getPsiElement().getContainingFile() != null) {
            final PsiFile file = location.getPsiElement().getContainingFile();
            if (!(file instanceof Perl6File)) return false;
            final VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null) return false;
            if (virtualFile instanceof LightVirtualFile) return false;
            pathHolder.set(virtualFile.getPath());
        }
        else {
            VirtualFile directory = location.getVirtualFile();
            if (directory == null || !directory.isDirectory()) {
                return false;
            }
            VfsUtilCore.processFilesRecursively(directory, file -> {
                if (Arrays.asList("t", "t6", "rakutest").contains(file.getExtension())) {
                    pathHolder.set(directory.getPath());
                    return false;
                }
                return true;
            });
        }

        String path = pathHolder.get();
        return path != null && path.equals(configuration.getTestKind() == RakuTestKind.FILE
                                           ? configuration.getFilePath()
                                           : configuration.getDirectoryPath());
    }
}
