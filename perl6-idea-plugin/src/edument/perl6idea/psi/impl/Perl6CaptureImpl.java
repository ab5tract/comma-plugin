package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.psi.Perl6Capture;
import org.jetbrains.annotations.NotNull;

public class Perl6CaptureImpl extends ASTWrapperPsiElement implements Perl6Capture {
    public Perl6CaptureImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull String inferType() {
        return "Capture";
    }
}
