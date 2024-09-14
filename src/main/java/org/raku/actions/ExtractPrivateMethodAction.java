package org.raku.actions;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.actions.BasePlatformRefactoringAction;
import org.raku.refactoring.RakuCodeBlockType;
import org.raku.refactoring.RakuExtractCodeBlockHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExtractPrivateMethodAction extends BasePlatformRefactoringAction {
    public  ExtractPrivateMethodAction() {
        setInjectedContext(true);
    }

    @Nullable
    @Override
    protected RefactoringActionHandler getRefactoringHandler(@NotNull RefactoringSupportProvider provider) {
        return new RakuExtractCodeBlockHandler(RakuCodeBlockType.PRIVATEMETHOD);
    }

    @Override
    protected boolean isAvailableInEditorOnly() {
        return true;
    }
}
