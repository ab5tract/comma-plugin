package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RoleSignature;
import org.jetbrains.annotations.NotNull;

public class Perl6RoleSignatureImpl extends ASTWrapperPsiElement implements Perl6RoleSignature {
    public Perl6RoleSignatureImpl(@NotNull ASTNode node) {
        super(node);
    }
}
