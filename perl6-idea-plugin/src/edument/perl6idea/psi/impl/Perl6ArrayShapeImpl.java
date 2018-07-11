package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ArrayShape;
import org.jetbrains.annotations.NotNull;

public class Perl6ArrayShapeImpl extends ASTWrapperPsiElement implements Perl6ArrayShape {
    public Perl6ArrayShapeImpl(@NotNull ASTNode node) {
        super(node);
    }
}
