package edument.perl6idea.rename;

import com.intellij.psi.PsiElement;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6ModuleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6ModuleRenameProcessor extends RenamePsiElementProcessor {
    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return element instanceof Perl6File;
    }

    @Override
    public void renameElement(@NotNull PsiElement element,
                              @NotNull String newName,
                              @NotNull UsageInfo[] usages,
                              @Nullable RefactoringElementListener listener) throws IncorrectOperationException {
        Perl6File file = (Perl6File)element;
        PsiMetaData metaData = file.getMetaData();
        if (metaData == null) return;
        String name = metaData.getName();
        // Count old name and new name to see if we need to work with directory move
        String[] oldNameParts = name.split("::");
        String[] newNameParts = newName.split("::");
        // It is the same "package", so just rename file
        if (oldNameParts.length == newNameParts.length) {
            file.setName(newNameParts[newNameParts.length - 1] + ".pm6");
        } else {
            // FIXME
        }
        for (UsageInfo usage : usages) {
            boolean isModuleName = usage.getElement() instanceof Perl6ModuleName;
            if (isModuleName) {
                Perl6ModuleName moduleName = (Perl6ModuleName)usage.getElement();
                moduleName.setName(newName);
            }
        }
    }

    @Override
    public boolean isInplaceRenameSupported() {
        return false;
    }
}
