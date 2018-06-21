package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6StubCode;
import org.jetbrains.annotations.NotNull;

public class Perl6StubCodeImpl extends ASTWrapperPsiElement implements Perl6StubCode {
    public Perl6StubCodeImpl(@NotNull ASTNode node) {
        super(node);
    }
}
