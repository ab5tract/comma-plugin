package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexCClass;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexCClassImpl extends ASTWrapperPsiElement implements Perl6RegexCClass {
    public Perl6RegexCClassImpl(@NotNull ASTNode node) {
        super(node);
    }
}
