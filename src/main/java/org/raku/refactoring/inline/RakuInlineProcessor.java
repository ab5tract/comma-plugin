package org.raku.refactoring.inline;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.BaseRefactoringProcessor;
import org.raku.psi.*;
import org.jetbrains.annotations.NotNull;

public abstract class RakuInlineProcessor extends BaseRefactoringProcessor {
    protected RakuInlineProcessor(@NotNull Project project) {
        super(project);
    }

    protected static boolean checkIfNeedToWrap(PsiElement initializer) {
        boolean isSingular = initializer instanceof RakuVariable ||
                             initializer instanceof RakuColonPair ||
                             initializer instanceof RakuComplexLiteral ||
                             initializer instanceof RakuIntLiteral ||
                             initializer instanceof RakuNumLiteral ||
                             initializer instanceof RakuRatLiteral ||
                             initializer instanceof RakuRegexLiteral ||
                             initializer instanceof RakuStrLiteral ||
                             initializer instanceof RakuContextualizer ||
                             initializer instanceof RakuArrayComposer ||
                             initializer instanceof RakuBlockOrHash;
        return !isSingular;
    }
}
