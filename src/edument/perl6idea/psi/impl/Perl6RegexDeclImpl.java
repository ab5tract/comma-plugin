package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6RegexDecl;
import edument.perl6idea.psi.Perl6SymbolLike;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexDeclImpl extends Perl6SymbolLike implements Perl6RegexDecl {
    public Perl6RegexDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getRegexKind() {
        PsiElement declarator = findChildByType(Perl6TokenTypes.REGEX_DECLARATOR);
        return declarator == null ? "rule" : declarator.getText();
    }

    @Override
    public String getRegexName() {
        PsiElement name = findChildByType(Perl6ElementTypes.LONG_NAME);
        return name == null ? "<anon>" : name.getText();
    }
}
