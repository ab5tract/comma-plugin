package org.raku.testing;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import org.raku.psi.RakuFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicReference;

public class RakuCompleteTestRunConfigurationProducer extends LazyRunConfigurationProducer<RakuTestRunConfiguration> {
    @NotNull
    @Override
    public ConfigurationFactory getConfigurationFactory() {
        return RakuCompleteTestConfigurationType.getInstance().getFactory();
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull RakuTestRunConfiguration configuration,
                                                    @NotNull ConfigurationContext context,
                                                    @NotNull Ref<PsiElement> sourceElement) {
        final Location location = context.getLocation();
        if (location == null) return false;
        VirtualFile fileToTest;
        if (location.getPsiElement().getContainingFile() != null) {
            final PsiFile file = location.getPsiElement().getContainingFile();
            if (!(file instanceof RakuFile)) return false;
            final VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null ||
                virtualFile instanceof LightVirtualFile ||
                !Arrays.asList("t", "t6", "rakutest").contains(virtualFile.getExtension())) {
                return false;
            }
            configuration.setTestKind(RakuTestKind.FILE);
            configuration.setFilePath(virtualFile.getPath());
            fileToTest = virtualFile;
        }
        else {
            VirtualFile directory = location.getVirtualFile();
            if (directory == null || !directory.isDirectory()) return false;
            AtomicReference<String> pathHolder = new AtomicReference<>();
            VfsUtilCore.processFilesRecursively(directory, file -> {
                if (Arrays.asList("t", "t6", "rakutest").contains(file.getExtension())) {
                    pathHolder.set(directory.getPath());
                    return false;
                }
                return true;
            });
            String path = pathHolder.get();
            if (path == null) return false;
            configuration.setTestKind(RakuTestKind.DIRECTORY);
            configuration.setDirectoryPath(path);
            fileToTest = directory;
        }
        Module module = ModuleUtilCore.findModuleForFile(fileToTest, context.getProject());
        if (module == null) return false;
        ContentEntry[] contentEntries = ModuleRootManager.getInstance(module).getContentEntries();
        if (contentEntries.length != 1 || contentEntries[0].getFile() == null) return false;
        configuration.setInterpreterArguments(calculateParameters(contentEntries[0]));
        configuration.setName(configuration.suggestedName());
        return true;
    }

    private static String calculateParameters(ContentEntry contentEntryToTest) {
        VirtualFile rakuModuleRoot = contentEntryToTest.getFile();
        if (rakuModuleRoot == null)
            return "";
        StringJoiner argsLine = new StringJoiner(" ");
        Arrays.stream(contentEntryToTest.getSourceFolders()).forEachOrdered((sourceFolder) -> {
            if (sourceFolder.isTestSource()) return;
            VirtualFile dir = sourceFolder.getFile();
            if (dir == null) return;
            String rootPath = rakuModuleRoot.getPath();
            if (rootPath.length() != 0)
                argsLine.add("-I" + dir.getPath().substring(rootPath.length() + 1));
        });
        return argsLine.toString();
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull RakuTestRunConfiguration configuration, @NotNull ConfigurationContext context) {
        if (configuration.getTestKind() != RakuTestKind.FILE && configuration.getTestKind() != RakuTestKind.DIRECTORY)
            return false;

        final Location location = context.getLocation();
        if (location == null) return false;
        AtomicReference<VirtualFile> fileToTest = new AtomicReference<>();
        if (location.getPsiElement().getContainingFile() != null) {
            final PsiFile file = location.getPsiElement().getContainingFile();
            if (!(file instanceof RakuFile)) return false;
            final VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null ||
                virtualFile instanceof LightVirtualFile ||
                !Arrays.asList("t", "t6", "rakutest").contains(virtualFile.getExtension())) {
                return false;
            }
            fileToTest.set(virtualFile);
        }
        else {
            VirtualFile directory = location.getVirtualFile();
            if (directory == null || !directory.isDirectory()) {
                return false;
            }
            VfsUtilCore.processFilesRecursively(directory, file -> {
                if (Arrays.asList("t", "t6", "rakutest").contains(file.getExtension())) {
                    fileToTest.set(directory);
                    return false;
                }
                return true;
            });
        }

        VirtualFile virtualFile = fileToTest.get();
        if (virtualFile == null) return false;
        Module module = ModuleUtilCore.findModuleForFile(virtualFile, context.getProject());
        if (module == null) return false;
        ContentEntry[] contentEntries = ModuleRootManager.getInstance(module).getContentEntries();
        if (contentEntries.length != 1 || contentEntries[0].getFile() == null) return false;
        return virtualFile.getPath().equals(configuration.getTestKind() == RakuTestKind.FILE
                                            ? configuration.getFilePath()
                                            : configuration.getDirectoryPath());
    }
}
