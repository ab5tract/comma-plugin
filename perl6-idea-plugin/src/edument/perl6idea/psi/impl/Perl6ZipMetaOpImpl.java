package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ZipMetaOp;
import org.jetbrains.annotations.NotNull;

public class Perl6ZipMetaOpImpl extends ASTWrapperPsiElement implements Perl6ZipMetaOp {
    public Perl6ZipMetaOpImpl(@NotNull ASTNode node) {
        super(node);
    }
}
