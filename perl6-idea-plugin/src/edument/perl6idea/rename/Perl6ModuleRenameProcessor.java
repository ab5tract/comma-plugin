package edument.perl6idea.rename;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.filetypes.Perl6ModuleFileType;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Perl6ModuleRenameProcessor extends RenamePsiElementProcessor {
    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        if (!(element instanceof Perl6File)) return false;
        // Process only module files, no scripts or tests
        if (!(FileTypeManager.getInstance().getFileTypeByFile(element.getContainingFile().getVirtualFile()) instanceof Perl6ModuleFileType))
            return false;
        Perl6File file = (Perl6File)element;
        Module module = ModuleUtilCore.findModuleForFile(file);
        if (module == null) return false;
        VirtualFile[] roots = ModuleRootManager.getInstance(module).getSourceRoots();
        for (VirtualFile root : roots) {
            if (root.getName().equals("lib") &&
                file.getVirtualFile().getPath().startsWith(root.getPath())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void renameElement(@NotNull PsiElement element,
                              @NotNull String newPackageName,
                              UsageInfo @NotNull [] usages,
                              @Nullable RefactoringElementListener listener) throws IncorrectOperationException {
        Perl6File file = (Perl6File)element;
        PsiMetaData metaData = file.getMetaData();
        if (metaData == null) return;
        String oldPackageName = metaData.getName();
        // Count old oldPackageName and new oldPackageName to see if we need to work with directory move
        String[] oldNameParts = oldPackageName.split("::");
        String[] newNameParts = newPackageName.split("::");
        handleFileMove(file, oldNameParts, newNameParts);
        for (UsageInfo usage : usages) {
            boolean isModuleName = usage.getElement() instanceof Perl6ModuleName;
            if (isModuleName) {
                Perl6ModuleName moduleName = (Perl6ModuleName)usage.getElement();
                moduleName.setName(newPackageName);
            }
        }
    }

    private void handleFileMove(Perl6File file, String[] oldNameArray, String[] newNameArray) {
        List<String> oldName = new ArrayList<>(Arrays.asList(oldNameArray));
        oldName.remove(oldName.size() - 1);
        List<String> newName = new ArrayList<>(Arrays.asList(newNameArray));
        // Yes, Java has no Path.extension method
        int i = file.getName().lastIndexOf(".");
        String extension = i >= 0 ? file.getName().substring(i + 1) : null;
        String newFileName = newName.get(newName.size() - 1) + "." +
                             (extension != null ? extension : Perl6ModuleFileType.INSTANCE.getDefaultExtension());
        newName.remove(newName.size() - 1);
        VirtualFile directoryToCleanupAfter = file.getVirtualFile().getParent();

        if (oldName.equals(newName)) {
            file.setName(newFileName);
            // If it is only one file that changes its name, just rename it and exit
            return;
        }

        VirtualFile fileVirtualFile = file.getVirtualFile();
        String stringPathToOldFile = fileVirtualFile.getPath();
        Path pathToOldFile = Paths.get(stringPathToOldFile);
        for (String ignored : oldNameArray) {
            pathToOldFile = pathToOldFile.getParent();
        }
        String[] betweenRootAndFile = Arrays.copyOfRange(newNameArray, 0, newNameArray.length - 1);
        Path newDirectory = Paths.get(pathToOldFile.toString(), betweenRootAndFile);
        try {
            Path directoryToMoveTo = Files.createDirectories(newDirectory);
            VirtualFile virtualDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(directoryToMoveTo.toFile());
            if (virtualDir == null) {
                throw new IncorrectOperationException();
            }
            fileVirtualFile.move(this, virtualDir);
            file.setName(newFileName);
            cleanupOldPath(directoryToCleanupAfter, oldName);
        }
        catch (IOException e) {
            Logger.getInstance(Perl6ModuleRenameProcessor.class).error(e);
        }
    }

    private void cleanupOldPath(VirtualFile innermostDir, List<String> name) throws IOException {
        VirtualFile temp;
        for (int i = name.size() - 1; i > 0; i--) {
            VirtualFile[] children = innermostDir.getChildren();
            if (children.length != 0) break;
            temp = innermostDir.getParent();
            innermostDir.delete(this);
            innermostDir = temp;
        }
    }

    @Override
    public boolean isInplaceRenameSupported() {
        return false;
    }
}
