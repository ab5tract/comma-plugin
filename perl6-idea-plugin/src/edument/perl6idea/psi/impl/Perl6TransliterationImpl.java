package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Transliteration;
import org.jetbrains.annotations.NotNull;

public class Perl6TransliterationImpl extends ASTWrapperPsiElement implements Perl6Transliteration {
    public Perl6TransliterationImpl(@NotNull ASTNode node) {
        super(node);
    }
}
