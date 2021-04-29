package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.PodBlockAbbreviated;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PodBlockAbbreviatedImpl extends ASTWrapperPsiElement implements PodBlockAbbreviated {
    public PodBlockAbbreviatedImpl(@NotNull ASTNode node) {
        super(node);
    }
}
