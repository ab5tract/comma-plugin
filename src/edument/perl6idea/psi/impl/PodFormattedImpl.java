package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.PodFormatted;
import org.jetbrains.annotations.NotNull;

public class PodFormattedImpl extends ASTWrapperPsiElement implements PodFormatted {
    public PodFormattedImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getFormatCode() {
        PsiElement code = findChildByType(Perl6TokenTypes.FORMAT_CODE);
        return code == null ? "" : code.getText();
    }

    @Override
    public TextRange getFormattedTextRange() {
        PsiElement starter = findChildByType(Perl6TokenTypes.POD_FORMAT_STARTER);
        PsiElement stopper = findChildByType(Perl6TokenTypes.POD_FORMAT_STOPPER);
        int startOffset = starter.getTextOffset() + starter.getTextLength();
        return new TextRange(startOffset, stopper != null ? stopper.getTextOffset() : startOffset);
    }
}
