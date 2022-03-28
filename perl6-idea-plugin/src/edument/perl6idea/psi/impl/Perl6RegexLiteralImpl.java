package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexLiteral;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexLiteralImpl extends ASTWrapperPsiElement implements Perl6RegexLiteral {
    public Perl6RegexLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean mightMatchZeroWidth() {
        return getTextLength() == 0;
    }
}
