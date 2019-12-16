package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6QuoteRegex;
import org.jetbrains.annotations.NotNull;

public class Perl6QuoteRegexImpl extends ASTWrapperPsiElement implements Perl6QuoteRegex {
    public Perl6QuoteRegexImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String inferType() {
        return "Regex";
    }
}
