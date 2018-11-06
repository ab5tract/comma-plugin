package edument.perl6idea.refactoring;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringActionHandler;
import edument.perl6idea.psi.Perl6ParameterVariable;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.refactoring.introduce.constant.Perl6IntroduceConstantHandler;
import edument.perl6idea.refactoring.introduce.variable.Perl6IntroduceVariableHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6RefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof Perl6VariableDecl || element instanceof Perl6ParameterVariable;
    }

    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceVariableHandler() {
        return new Perl6IntroduceVariableHandler(null, "Extract Variable");
    }

    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceConstantHandler() {
        return new Perl6IntroduceConstantHandler(null, "Extract Constant");
    }

    @Override
    public boolean isInplaceIntroduceAvailable(@NotNull PsiElement element, PsiElement context) {
        return true;
    }
}
