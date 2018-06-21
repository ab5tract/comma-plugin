package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6TermDefinition;
import org.jetbrains.annotations.NotNull;

public class Perl6TermDefinitionImpl extends ASTWrapperPsiElement implements Perl6TermDefinition {
    public Perl6TermDefinitionImpl(@NotNull ASTNode node) {
        super(node);
    }
}
