package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Version;
import org.jetbrains.annotations.NotNull;

public class Perl6VersionImpl extends ASTWrapperPsiElement implements Perl6Version {
    public Perl6VersionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull String inferType() {
        return "Version";
    }
}
