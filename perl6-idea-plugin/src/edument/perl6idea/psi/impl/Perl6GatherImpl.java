package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Gather;
import org.jetbrains.annotations.NotNull;

public class Perl6GatherImpl extends ASTWrapperPsiElement implements Perl6Gather {
    public Perl6GatherImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull String inferType() {
        return "Seq";
    }
}
