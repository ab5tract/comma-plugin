package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6ArrayComposer;
import org.jetbrains.annotations.NotNull;

public class Perl6ArrayComposerImpl extends ASTWrapperPsiElement implements Perl6ArrayComposer {
    public Perl6ArrayComposerImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String inferType() {
        return "Array";
    }
}
