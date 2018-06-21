package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexQuantifier;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexQuantifierImpl extends ASTWrapperPsiElement implements Perl6RegexQuantifier {
    public Perl6RegexQuantifierImpl(@NotNull ASTNode node) {
        super(node);
    }
}
