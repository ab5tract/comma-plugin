package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RadixNumber;
import org.jetbrains.annotations.NotNull;

public class Perl6RadixNumberImpl extends ASTWrapperPsiElement implements Perl6RadixNumber {
    public Perl6RadixNumberImpl(@NotNull ASTNode node) {
        super(node);
    }
}
