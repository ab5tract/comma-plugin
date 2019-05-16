package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public class Perl6ConditionalBranch {
    public PsiElement term;
    @Nullable
    public PsiElement condition;
    @Nullable
    public Perl6Block block;

    public Perl6ConditionalBranch(PsiElement term, @Nullable PsiElement condition, @Nullable Perl6Block block) {
        this.term = term;
        this.condition = condition;
        this.block = block;
    }
}
