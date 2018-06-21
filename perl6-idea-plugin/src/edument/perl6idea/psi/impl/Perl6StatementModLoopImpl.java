package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6StatementModLoop;
import org.jetbrains.annotations.NotNull;

public class Perl6StatementModLoopImpl extends ASTWrapperPsiElement implements Perl6StatementModLoop {
    public Perl6StatementModLoopImpl(@NotNull ASTNode node) {
        super(node);
    }
}
