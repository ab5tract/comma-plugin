package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.PodFormatted;
import org.apache.commons.lang.StringEscapeUtils;
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

    @Override
    public String renderPod() {
        String opener = "";
        String closer = "";
        boolean format = true;
        switch (getFormatCode()) {
            case "B": opener = "<strong>"; closer = "</strong>"; break;
            case "C": opener = "<code>"; closer = "</code>"; break;
            case "I": opener = "<em>"; closer = "</em>"; break;
            case "K": opener = "<kbd>"; closer = "</kbd>"; break;
            case "R": opener = "<var>"; closer = "</var>"; break;
            case "T": opener = "<samp>"; closer = "</samp>"; break;
            case "U": opener = "<u>"; closer = "</u>"; break;
            case "V": format = false; break;
            case "Z": return ""; // It's a comment
        }
        StringBuilder builder = new StringBuilder();
        builder.append(opener);
        PsiElement content = findChildByType(Perl6ElementTypes.POD_TEXT);
        if (content != null)
            for (ASTNode child : content.getNode().getChildren(TokenSet.ANY))
                if (format && child.getElementType() == Perl6ElementTypes.POD_FORMATTED)
                    builder.append(((PodFormatted)child.getPsi()).renderPod());
                else
                    builder.append(StringEscapeUtils.escapeHtml(child.getText()));
        builder.append(closer);
        return builder.toString();
    }
}
