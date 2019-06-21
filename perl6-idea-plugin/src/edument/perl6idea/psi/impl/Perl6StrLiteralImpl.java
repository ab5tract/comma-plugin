package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6StrLiteral;
import org.jetbrains.annotations.NotNull;

public class Perl6StrLiteralImpl extends ASTWrapperPsiElement implements Perl6StrLiteral {
    public Perl6StrLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String inferType() {
        return "Str";
    }

    @Override
    public String getStringText() {
        PsiElement opener = findChildByType(Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN);
        PsiElement closer = findChildByType(Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE);
        int start = opener != null ? opener.getStartOffsetInParent() + opener.getTextLength() : 0;
        int end = closer != null ? closer.getStartOffsetInParent() : this.getTextLength();
        return getText().substring(start, end);
    }
}
