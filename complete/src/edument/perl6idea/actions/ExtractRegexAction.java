package edument.perl6idea.actions;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.actions.BasePlatformRefactoringAction;
import edument.perl6idea.refactoring.Perl6ExtractRegexPartHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExtractRegexAction extends BasePlatformRefactoringAction {
    @Nullable
    @Override
    protected RefactoringActionHandler getRefactoringHandler(@NotNull RefactoringSupportProvider provider) {
        return new Perl6ExtractRegexPartHandler();
    }

    @Override
    protected boolean isAvailableInEditorOnly() {
        return true;
    }
}
