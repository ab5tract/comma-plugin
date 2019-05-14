package edument.perl6idea.refactoring.inline;

import com.intellij.lang.refactoring.InlineHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6Statement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6CallInlineHandler implements InlineHandler {
    @Nullable
    @Override
    public Settings prepareInlineElement(@NotNull PsiElement element, @Nullable Editor editor, boolean invokedOnReference) {
        if (element instanceof Perl6RoutineDecl) {
            return Perl6InlineRoutineUtil.inlineRoutineSettings((Perl6RoutineDecl) element, editor, invokedOnReference);
        }
        return null;
    }

    @Override
    public void removeDefinition(@NotNull PsiElement element, @NotNull Settings settings) {
        Perl6Statement statement = PsiTreeUtil.getParentOfType(element, Perl6Statement.class);
        PsiElement after = statement.getNextSibling();
        statement.delete();
        if (after instanceof PsiWhiteSpace)
            after.delete();
    }

    @Nullable
    @Override
    public Inliner createInliner(@NotNull PsiElement element, @NotNull Settings settings) {
        return element instanceof Perl6RoutineDecl ? new Perl6RoutineInliner() : null;
    }
}
