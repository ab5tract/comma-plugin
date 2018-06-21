package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexBuiltinCClass;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexBuiltinCClassImpl extends ASTWrapperPsiElement implements Perl6RegexBuiltinCClass {
    public Perl6RegexBuiltinCClassImpl(@NotNull ASTNode node) {
        super(node);
    }
}
