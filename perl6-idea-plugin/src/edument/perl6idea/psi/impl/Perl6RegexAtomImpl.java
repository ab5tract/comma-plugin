package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6RegexAtom;
import edument.perl6idea.psi.Perl6RegexQuantifier;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexAtomImpl extends ASTWrapperPsiElement implements Perl6RegexAtom {
    public Perl6RegexAtomImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean mightMatchZeroWidth() {
        // See if it is quantified with ? or *, which can trivially match zero
        // width.
        Perl6RegexQuantifier quantifier = PsiTreeUtil.getChildOfType(this, Perl6RegexQuantifier.class);
        if (quantifier != null) {
            String quantText = quantifier.getFirstChild().getText();
            if (quantText.equals("?") || quantText.equals("*"))
                return true;
        }

        // Otherwise, not sure.
        return false;
    }
}
