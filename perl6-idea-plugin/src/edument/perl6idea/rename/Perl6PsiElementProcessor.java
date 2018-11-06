package edument.perl6idea.rename;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6PsiElementProcessor extends RenamePsiElementProcessor {
    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return element instanceof Perl6PsiElement && !(element instanceof Perl6File);
    }

    @NotNull
    @Override
    public RenameDialog createRenameDialog(@NotNull Project project,
                                           @NotNull PsiElement element,
                                           @Nullable PsiElement nameSuggestionContext,
                                           @Nullable Editor editor) {
        return new Perl6RenameDialog(project, element, nameSuggestionContext, editor);
    }
}
