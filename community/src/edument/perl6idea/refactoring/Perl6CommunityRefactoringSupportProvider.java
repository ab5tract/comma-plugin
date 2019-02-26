package edument.perl6idea.refactoring;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringActionHandler;
import edument.perl6idea.psi.Perl6ParameterVariable;
import edument.perl6idea.psi.Perl6VariableDecl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class Perl6CommunityRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof Perl6VariableDecl || element instanceof Perl6ParameterVariable;
    }

    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceVariableHandler() {
        return null;
    }

    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceConstantHandler() {
        return null;
    }

    @Nullable
    @Override
    public RefactoringActionHandler getExtractMethodHandler() {
        return null;
    }

    @Override
    public boolean isInplaceIntroduceAvailable(@NotNull PsiElement element, PsiElement context) {
        return true;
    }
}
