package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Sink;
import org.jetbrains.annotations.NotNull;

public class Perl6SinkImpl extends ASTWrapperPsiElement implements Perl6Sink {
    public Perl6SinkImpl(@NotNull ASTNode node) {
        super(node);
    }
}
