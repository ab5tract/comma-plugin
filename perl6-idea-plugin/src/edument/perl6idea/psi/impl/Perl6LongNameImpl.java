package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ColonPair;
import edument.perl6idea.psi.Perl6LongName;
import org.jetbrains.annotations.NotNull;

public class Perl6LongNameImpl extends ASTWrapperPsiElement implements Perl6LongName {
    public Perl6LongNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getNameWithoutColonPairs() {
        Perl6ColonPair firstColonPair = findChildByClass(Perl6ColonPair.class);
        return firstColonPair == null
               ? getText()
               : getText().substring(0, firstColonPair.getStartOffsetInParent());
    }

    @Override
    @NotNull
    public Perl6ColonPair[] getColonPairs() {
        return findChildrenByClass(Perl6ColonPair.class);
    }
}
