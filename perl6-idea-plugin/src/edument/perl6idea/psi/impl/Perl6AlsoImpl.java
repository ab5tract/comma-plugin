package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Also;
import org.jetbrains.annotations.NotNull;

public class Perl6AlsoImpl extends ASTWrapperPsiElement implements Perl6Also {
    public Perl6AlsoImpl(@NotNull ASTNode node) {
        super(node);
    }
}
