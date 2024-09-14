package org.raku.comma.rename;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import org.raku.comma.psi.RakuFile;
import org.raku.comma.psi.RakuPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class RakuPsiElementProcessor extends RenamePsiElementProcessor {
    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return element instanceof RakuPsiElement && !(element instanceof RakuFile);
    }

    @NotNull
    @Override
    public RenameDialog createRenameDialog(@NotNull Project project,
                                           @NotNull PsiElement element,
                                           @Nullable PsiElement nameSuggestionContext,
                                           @Nullable Editor editor) {
        return new RakuRenameDialog(project, element, nameSuggestionContext, editor);
    }
}
