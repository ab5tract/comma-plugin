package org.raku.comma.refactoring;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringActionHandler;
import org.raku.comma.psi.RakuParameterVariable;
import org.raku.comma.psi.RakuVariableDecl;
import org.raku.comma.refactoring.introduce.constant.RakuIntroduceConstantHandler;
import org.raku.comma.refactoring.introduce.variable.RakuIntroduceVariableHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RakuCompleteRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof RakuVariableDecl || element instanceof RakuParameterVariable;
    }

    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceVariableHandler() {
        return new RakuIntroduceVariableHandler(null, "Extract Variable");
    }

    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceConstantHandler() {
        return new RakuIntroduceConstantHandler(null, "Extract Constant");
    }

    @Nullable
    @Override
    public RefactoringActionHandler getExtractMethodHandler() {
        return new RakuExtractCodeBlockHandler(RakuCodeBlockType.METHOD);
    }

    @Override
    public boolean isInplaceIntroduceAvailable(@NotNull PsiElement element, PsiElement context) {
        return true;
    }
}
