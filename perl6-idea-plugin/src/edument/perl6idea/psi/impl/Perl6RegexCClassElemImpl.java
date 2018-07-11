package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexCClassElem;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexCClassElemImpl extends ASTWrapperPsiElement implements Perl6RegexCClassElem {
    public Perl6RegexCClassElemImpl(@NotNull ASTNode node) {
        super(node);
    }
}
