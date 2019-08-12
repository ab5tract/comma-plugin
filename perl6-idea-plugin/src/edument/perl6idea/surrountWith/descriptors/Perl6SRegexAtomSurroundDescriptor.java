package edument.perl6idea.surrountWith.descriptors;

import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class Perl6SRegexAtomSurroundDescriptor implements SurroundDescriptor {
    @NotNull
    @Override
    public PsiElement[] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
        return new PsiElement[0];
    }

    @NotNull
    @Override
    public Surrounder[] getSurrounders() {
        return new Surrounder[0];
    }

    @Override
    public boolean isExclusive() {
        return false;
    }
}
