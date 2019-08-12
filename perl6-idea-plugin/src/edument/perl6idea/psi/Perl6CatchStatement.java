package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public interface Perl6CatchStatement extends Perl6PsiElement, P6Control {
    @Nullable
    @Override
    default PsiElement getTopic() {
        return null;
    }
}
