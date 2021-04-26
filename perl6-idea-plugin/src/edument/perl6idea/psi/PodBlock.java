package edument.perl6idea.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public interface PodBlock extends PodElement {
    TokenSet POD_CONTENT = TokenSet.create(
            Perl6ElementTypes.POD_BLOCK_ABBREVIATED,
            Perl6ElementTypes.POD_BLOCK_DELIMITED,
            Perl6ElementTypes.POD_BLOCK_PARAGRAPH,
            Perl6ElementTypes.POD_FORMATTED,
            Perl6TokenTypes.POD_TEXT,
            Perl6TokenTypes.POD_CODE,
            Perl6TokenTypes.POD_NEWLINE);

    @Nullable
    default String getTypename() {
        ASTNode typenameNode = getNode().findChildByType(Perl6TokenTypes.POD_TYPENAME);
        return typenameNode != null ? typenameNode.getText() : null;
    }

    default ASTNode @NotNull [] getContent() {
        return getNode().getChildren(POD_CONTENT);
    }

    default String renderPod(PodRenderingContext context) {
        // Classify by type, and set opener/closer for those that have one.
        String typename = getTypename();
        String opener = null;
        String closer = null;
        boolean isSemantic = false;
        boolean forceCode = false;
        switch (typename != null ? typename : "") {
            case "head1": opener = "<h1>"; closer = "</h1>"; break;
            case "head2": opener = "<h2>"; closer = "</h2>"; break;
            case "head3": opener = "<h3>"; closer = "</h3>"; break;
            case "head4": opener = "<h4>"; closer = "</h4>"; break;
            case "head5": opener = "<h5>"; closer = "</h5>"; break;
            case "head6": opener = "<h6>"; closer = "</h6>"; break;
            case "para": opener = "<p>"; closer = "</p>"; break;
            case "code": case "input": case "output": forceCode = true; break;
            case "comment": return "";
            default:
                isSemantic = typename != null && typename.toUpperCase(Locale.ROOT).equals(typename);
        }

        // Work through the content.
        StringBuilder builder = new StringBuilder();
        boolean needOpener = true;
        int outstandingNewlines = 0;
        int codeWhitespaceToStrip = 0;
        for (ASTNode node : getContent()) {
            // Never emit removed whitespace at the start of lines.
            if (node.getElementType() == Perl6TokenTypes.POD_REMOVED_WHITESPACE)
                continue;

            // Handle newlines, multiple of which may separate code blocks or
            // paragraphs.
            if (node.getElementType() == Perl6TokenTypes.POD_NEWLINE) {
                outstandingNewlines++;
                continue;
            }
            else {
                if (outstandingNewlines == 1) {
                    builder.append('\n');
                }
                else if (outstandingNewlines > 1 && !needOpener) {
                    if (closer != null)
                        builder.append(closer);
                    needOpener = true;
                    opener = null;
                }
                outstandingNewlines = 0;
            }

            // Render nested blocks.
            PsiElement psi = node.getPsi();
            if (psi instanceof PodBlock) {
                builder.append(((PodBlock)psi).renderPod(context));
            }

            // If it's not a nested block, then we have raw content, and may need
            // an opener/closer too (for paragraph splits, code. etc.)
            else {
                if (needOpener) {
                    if (opener == null) {
                        if (forceCode || node.getElementType() == Perl6TokenTypes.POD_CODE) {
                            opener = "<pre><code>";
                            closer = "</code></pre>";
                            codeWhitespaceToStrip = countLeadingWhitespace(node.getText());
                        }
                        else {
                            opener = "<p>";
                            closer = "</p>";
                        }
                    }
                    builder.append(opener);
                }
                needOpener = false;
                if (forceCode)
                    builder.append(StringEscapeUtils.escapeHtml(psi.getText()));
                else if (psi instanceof PodFormatted)
                    builder.append(((PodFormatted)psi).renderPod());
                else if (node.getElementType() == Perl6TokenTypes.POD_CODE)
                    builder.append(StringEscapeUtils.escapeHtml(stripCodeWhitespace(psi.getText(), codeWhitespaceToStrip)));
                else
                    builder.append(StringEscapeUtils.escapeHtml(psi.getText()));
            }
        }
        if (!needOpener && closer != null)
            builder.append(closer);

        // Semantic blocks are stored for later, semantic ones are retained.
        if (isSemantic) {
            context.addSemanticBlock(typename, builder.toString());
            return "";
        }
        else {
            return builder.toString();
        }
    }

    private static int countLeadingWhitespace(String text) {
        int i = 0;
        while (i < text.length() && Character.isWhitespace(text.charAt(i)))
            i++;
        return i;
    }

    private static String stripCodeWhitespace(String text, int stripLimit) {
        int i = 0;
        while (i < stripLimit && i < text.length() && Character.isWhitespace(text.charAt(i)))
            i++;
        return text.substring(i);
    }
}
