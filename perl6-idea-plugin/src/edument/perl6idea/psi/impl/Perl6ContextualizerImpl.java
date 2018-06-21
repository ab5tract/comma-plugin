package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Contextualizer;
import org.jetbrains.annotations.NotNull;

public class Perl6ContextualizerImpl extends ASTWrapperPsiElement implements Perl6Contextualizer {
    public Perl6ContextualizerImpl(@NotNull ASTNode node) {
        super(node);
    }
}
