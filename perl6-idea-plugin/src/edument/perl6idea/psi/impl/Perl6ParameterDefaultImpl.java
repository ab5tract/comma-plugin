package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ParameterDefault;
import org.jetbrains.annotations.NotNull;

public class Perl6ParameterDefaultImpl extends ASTWrapperPsiElement implements Perl6ParameterDefault {
    public Perl6ParameterDefaultImpl(@NotNull ASTNode node) {
        super(node);
    }
}
