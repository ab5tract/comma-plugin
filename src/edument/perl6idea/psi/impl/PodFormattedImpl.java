package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.PodFormatted;
import org.jetbrains.annotations.NotNull;

public class PodFormattedImpl extends ASTWrapperPsiElement implements PodFormatted {
    public PodFormattedImpl(@NotNull ASTNode node) {
        super(node);
    }
}
