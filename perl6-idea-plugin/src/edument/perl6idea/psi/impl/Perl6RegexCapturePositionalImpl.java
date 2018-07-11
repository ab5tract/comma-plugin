package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6RegexCapturePositional;
import org.jetbrains.annotations.NotNull;

public class Perl6RegexCapturePositionalImpl extends ASTWrapperPsiElement implements Perl6RegexCapturePositional {
    public Perl6RegexCapturePositionalImpl(@NotNull ASTNode node) {
        super(node);
    }
}
