package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.Perl6ArrayComposer;
import edument.perl6idea.psi.Perl6SemiList;
import edument.perl6idea.psi.Perl6Statement;
import org.jetbrains.annotations.NotNull;

public class Perl6ArrayComposerImpl extends ASTWrapperPsiElement implements Perl6ArrayComposer {
    public Perl6ArrayComposerImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull String inferType() {
        return "Array";
    }
}
