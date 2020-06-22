package edument.perl6idea.inspection;

import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.NonAsciiCharactersInspection;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6InspectionSuppressor implements InspectionSuppressor {
    @Override
    public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
        return element instanceof Perl6PsiElement && toolId.equals(new NonAsciiCharactersInspection().getShortName());
    }

    @Override
    public SuppressQuickFix @NotNull [] getSuppressActions(@Nullable PsiElement element, @NotNull String toolId) {
        return SuppressQuickFix.EMPTY_ARRAY;
    }
}
