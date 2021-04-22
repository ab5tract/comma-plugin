package edument.perl6idea.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PodBlock extends PodElement {
    TokenSet POD_CONTENT = TokenSet.create(
            Perl6ElementTypes.POD_BLOCK_ABBREVIATED,
            Perl6ElementTypes.POD_BLOCK_DELIMITED,
            Perl6ElementTypes.POD_BLOCK_PARAGRAPH,
            Perl6ElementTypes.POD_FORMATTED,
            Perl6TokenTypes.POD_TEXT,
            Perl6TokenTypes.POD_NEWLINE);

    @Nullable
    default String getTypename() {
        ASTNode typenameNode = getNode().findChildByType(Perl6TokenTypes.POD_TYPENAME);
        return typenameNode != null ? typenameNode.getText() : null;
    }

    default ASTNode @NotNull [] getContent() {
        return getNode().getChildren(POD_CONTENT);
    }

    default String renderPod() {
        String typename = getTypename();
        String opener = "";
        String closer = "";
        switch (typename == null ? "para" : typename) {
            case "head1": opener = "<h1>"; closer = "</h1>"; break;
            case "head2": opener = "<h2>"; closer = "</h2>"; break;
            case "head3": opener = "<h3>"; closer = "</h3>"; break;
            case "head4": opener = "<h4>"; closer = "</h4>"; break;
            case "head5": opener = "<h5>"; closer = "</h5>"; break;
            case "head6": opener = "<h6>"; closer = "</h6>"; break;
            case "para": opener = "<p>"; closer = "</p>"; break;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(opener);
        int outstandingNewlines = 0;
        for (ASTNode node : getContent()) {
            if (node.getElementType() == Perl6TokenTypes.POD_NEWLINE) {
                outstandingNewlines++;
            }
            else {
                if (outstandingNewlines == 1) {
                    builder.append('\n');
                }
                else if (outstandingNewlines > 1) {
                    builder.append(closer);
                    builder.append("<p>");
                    closer = "</p>";
                }
                outstandingNewlines = 0;
            }
            PsiElement psi = node.getPsi();
            if (psi instanceof PodBlock)
                builder.append(((PodBlock)psi).renderPod());
            else if (psi instanceof PodFormatted)
                builder.append(((PodFormatted)psi).renderPod());
            else
                builder.append(StringEscapeUtils.escapeHtml(psi.getText()));
        }
        builder.append(closer);
        return builder.toString();
    }
}
