package edument.perl6idea.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.pod.*;
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

    @NotNull
    default PodDomNode buildPodDom(PodDomBuildingContext context) {
        // The result is the top-level thing we produce. For certain kinds of blocks
        // it is the direct holder of everything below. However, when it's a paragraph
        // block or similar, we want to produce multiple paragraphs at the same level,
        // and so we'll set this to an item list.
        PodDomInnerNode result;

        // By contrast, node is the thing we will place all leaves into. In many cases,
        // that will be a raw item list, since we don't want any extra wrapping of
        // paragraph tags inside a head tag when rendering.
        PodDomInnerNode node;

        // Select node type and create it.
        String typename = getTypename();
        boolean isSemantic = false;
        boolean forceCode = false;
        int startOffset = getTextOffset();
        switch (typename != null ? typename : "") {
            case "comment": return new PodDomNodeList(startOffset);
            case "head1":
                result = new PodDomHead(startOffset, 1);
                node = new PodDomNodeList(startOffset);
                break;
            case "head2":
                result = new PodDomHead(startOffset, 2);
                node = new PodDomNodeList(startOffset);
                break;
            case "head3":
                result = new PodDomHead(startOffset, 3);
                node = new PodDomNodeList(startOffset);
                break;
            case "head4":
                result = new PodDomHead(startOffset, 4);
                node = new PodDomNodeList(startOffset);
                break;
            case "head5":
                result = new PodDomHead(startOffset, 5);
                node = new PodDomNodeList(startOffset);
                break;
            case "head6":
                result = new PodDomHead(startOffset, 6);
                node = new PodDomNodeList(startOffset);
                break;
            case "para":
                result = new PodDomNodeList(startOffset);
                node = new PodDomPara(startOffset);
                break;
            case "item":
            case "item1":
                result = new PodDomItem(startOffset, 1);
                node = new PodDomNodeList(startOffset);
                break;
            case "item2":
                result = new PodDomItem(startOffset, 2);
                node = new PodDomNodeList(startOffset);
                break;
            case "item3":
                result = new PodDomItem(startOffset, 3);
                node = new PodDomNodeList(startOffset);
                break;
            case "item4":
                result = new PodDomItem(startOffset, 4);
                node = new PodDomNodeList(startOffset);
                break;
            case "item5":
                result = new PodDomItem(startOffset, 5);
                node = new PodDomNodeList(startOffset);
                break;
            case "item6":
                result = new PodDomItem(startOffset, 6);
                node = new PodDomNodeList(startOffset);
                break;
            case "code": case "input": case "output":
                result = new PodDomCode(startOffset);
                node = new PodDomNodeList(startOffset);
                forceCode = true;
                break;
            default:
                result = new PodDomNodeList(startOffset);
                node = new PodDomPara(startOffset);
                isSemantic = typename != null && typename.toUpperCase(Locale.ROOT).equals(typename);
        }

        // Add the initial node into the result list.
        result.addChild(node);

        // Go through the child nodes and process those.
        int outstandingNewlines = 0;
        int codeWhitespaceToStrip = 0;
        for (ASTNode child : getContent()) {
            // Discard removed whitespace at the start of lines.
            if (child.getElementType() == Perl6TokenTypes.POD_REMOVED_WHITESPACE)
                continue;

            // If it's a nested block, form the DOM. Also clear any current node
            // we're placing content into, as we'll need to start a new one.
            PsiElement psi = child.getPsi();
            if (psi instanceof PodBlock) {
                result.addChild(((PodBlock)psi).buildPodDom(context));
                node = null;
                outstandingNewlines = 0;
                continue;
            }

            // Handle newlines, which may need inserting into the output or may
            // indicate paragraph/code breaks.
            if (child.getElementType() == Perl6TokenTypes.POD_NEWLINE) {
                // Emit the newline if we're in an all-code block.
                if (forceCode) {
                    if (node != null)
                        node.addChild(new PodDomText(child.getStartOffset(), "\n"));
                }

                // Otherwise, just count them up.
                else {
                    outstandingNewlines++;
                }

                // And then we're done.
                continue;
            }
            else if (node != null) {
                // How many newlines did we see?
                if (outstandingNewlines == 1) {
                    // Just one. Always emit it as a literal newline (it should be one
                    // in code, it gets ths spacing right in text).
                    node.addChild(new PodDomText(child.getStartOffset(), "\n"));
                }
                else if (outstandingNewlines > 1) {
                    // Need to start a new node unless we were in code and we're going
                    // to continue with it after emitting he appropirate number of
                    // newlines.
                    if (node instanceof PodDomCode && child.getElementType() == Perl6TokenTypes.POD_CODE) {
                        node.addChild(new PodDomText(child.getStartOffset(), "\n".repeat(outstandingNewlines)));
                    }
                    else {
                        node = null;
                    }
                }
                outstandingNewlines = 0;
            }

            // Some kind of content. See if we need to make a new holding node.
            if (node == null) {
                if (child.getElementType() == Perl6TokenTypes.POD_CODE) {
                    node = new PodDomCode(child.getStartOffset());
                    codeWhitespaceToStrip = countLeadingWhitespace(child.getText());
                }
                else {
                    node = new PodDomPara(child.getStartOffset());
                }
                result.addChild(node);
                outstandingNewlines = 0;
            }

            // Now go by type of element.
            if (forceCode)
                node.addChild(new PodDomText(psi.getTextOffset(), psi.getText()));
            else if (psi instanceof PodFormatted)
                node.addChild(((PodFormatted)psi).buildPodDom());
            else if (child.getElementType() == Perl6TokenTypes.POD_CODE)
                node.addChild(new PodDomText(psi.getTextOffset(), stripCodeWhitespace(psi.getText(), codeWhitespaceToStrip)));
            else
                node.addChild(new PodDomText(psi.getTextOffset(), psi.getText()));
        }

        // If it's a semantic block, we need to store it away in the context and return
        // an empty list.
        if (isSemantic) {
            context.addSemanticBlock(typename, result);
            result = new PodDomNodeList(startOffset);
        }

        return result;
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
