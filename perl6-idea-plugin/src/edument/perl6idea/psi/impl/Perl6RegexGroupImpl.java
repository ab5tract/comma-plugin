package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.psi.Perl6RegexGroup;
import edument.perl6idea.psi.Perl6RegexPsiElement;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexGroupImpl extends ASTWrapperPsiElement implements Perl6RegexGroup {
    public Perl6RegexGroupImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean mightMatchZeroWidth() {
        // Everything in the group must potentially match nothing.
        for (PsiElement child : getChildren()) {
            // If it's not a known regex element, then can't analyze it.
            if (!(child instanceof Perl6RegexPsiElement))
                return false;

            // Otherwise, see if it might match zero width. If we can't be
            // sure it will, then have to assume it can match something.
            if (!((Perl6RegexPsiElement)child).mightMatchZeroWidth())
                return false;
        }

        // If everything could match zero width, then this is true of the
        // group as a whole.
        return true;
    }
}
