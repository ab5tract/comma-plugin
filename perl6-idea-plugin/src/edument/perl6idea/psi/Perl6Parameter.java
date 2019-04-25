package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface Perl6Parameter extends Perl6PsiElement, Perl6PsiDeclaration {
    String summary();
    String getVariableName();
    @Nullable
    PsiElement getInitializer();
}
