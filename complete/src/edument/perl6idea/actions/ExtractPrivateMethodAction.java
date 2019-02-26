package edument.perl6idea.actions;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.actions.BasePlatformRefactoringAction;
import edument.perl6idea.refactoring.Perl6CodeBlockType;
import edument.perl6idea.refactoring.Perl6ExtractCodeBlockHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExtractPrivateMethodAction extends BasePlatformRefactoringAction {
    public  ExtractPrivateMethodAction() {
        setInjectedContext(true);
    }

    @Nullable
    @Override
    protected RefactoringActionHandler getRefactoringHandler(@NotNull RefactoringSupportProvider provider) {
        return new Perl6ExtractCodeBlockHandler(Perl6CodeBlockType.PRIVATEMETHOD);
    }

    @Override
    protected boolean isAvailableInEditorOnly() {
        return true;
    }
}
