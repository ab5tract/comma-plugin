package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface Perl6ColonPair extends Perl6PsiElement {
    @Nullable String getKey();
    PsiElement getStatement();
}
