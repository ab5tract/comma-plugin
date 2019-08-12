package edument.perl6idea.surrountWith.descriptors;

import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import edument.perl6idea.surrountWith.descriptors.surrounder.*;
import org.jetbrains.annotations.NotNull;

public class Perl6ExpressionSurroundDescriptor implements SurroundDescriptor {
    private static final Surrounder[] SURROUNDERS = {
        //new Perl6IfSurrounder(false),
        //new Perl6WithSurrounder(false),
        //new Perl6UnlessSurrounder(false),
        //new Perl6WithoutSurrounder(false),
        //new Perl6GivenSurrounder(false),
        //new Perl6ForSurrounder(false),
        //new Perl6WheneverSurrounder(false),
        //new Perl6WhenSurrounder(false),
        //new Perl6TrySurrounder(false),
        //new Perl6TryCatchWhenSurrounder(false),
        //new Perl6TryCatchDefaultSurrounder(false),
        //new Perl6StartSurrounder(false),
        //new Perl6PointyBlockSurrounder(false),
        //new Perl6HashSurrounder(false),
        //new Perl6ArraySurrounder(false),
        //new Perl6ArrayContextSurrounder(false),
        //new Perl6HashContextSurrounder(false)
    };

    @NotNull
    @Override
    public PsiElement[] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
        return new PsiElement[0];
    }

    @NotNull
    @Override
    public Surrounder[] getSurrounders() {
        return SURROUNDERS;
    }

    @Override
    public boolean isExclusive() {
        return false;
    }
}
