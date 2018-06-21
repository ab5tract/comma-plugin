package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Phaser;
import org.jetbrains.annotations.NotNull;

public class Perl6PhaserImpl extends ASTWrapperPsiElement implements Perl6Phaser {
    public Perl6PhaserImpl(@NotNull ASTNode node) {
        super(node);
    }
}
