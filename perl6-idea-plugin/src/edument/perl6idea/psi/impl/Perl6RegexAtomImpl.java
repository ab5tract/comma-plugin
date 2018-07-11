package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexAtom;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexAtomImpl extends ASTWrapperPsiElement implements Perl6RegexAtom {
    public Perl6RegexAtomImpl(@NotNull ASTNode node) {
        super(node);
    }
}
