package org.raku.comma.inspection;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.NonAsciiCharactersInspection;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiElement;
import org.raku.comma.psi.RakuPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class RakuInspectionSuppressor implements InspectionSuppressor {
    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
        return element instanceof RakuPsiElement && toolId.equals(new NonAsciiCharactersInspection().getShortName());
    }

    @Override
    public SuppressQuickFix @NotNull [] getSuppressActions(@Nullable PsiElement element, @NotNull String toolId) {
        return SuppressQuickFix.EMPTY_ARRAY;
    }
}
