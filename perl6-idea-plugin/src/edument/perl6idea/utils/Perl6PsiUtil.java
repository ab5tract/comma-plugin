package edument.perl6idea.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import edument.perl6idea.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

import static edument.perl6idea.parsing.Perl6TokenTypes.UNV_WHITE_SPACE;

public class Perl6PsiUtil {
    /**
     * If node element is a white space one, get next or prev non-whitespace sibling
     * @param node starting node to move from
     * @param toRight direction of move, next sibling if true, prev otherwise
     * @return non-whitespace PsiElement or null
     */
    @Nullable
    public static PsiElement skipSpaces(PsiElement node, boolean toRight) {
        return skipSpaces(node, toRight, true);
    }

    @Nullable
    public static PsiElement skipSpaces(PsiElement node, boolean toRight, boolean skipComments) {
        PsiElement temp = node;
        while (temp != null &&
                (temp instanceof PsiWhiteSpace
                        || temp.getNode().getElementType().equals(UNV_WHITE_SPACE)
                        || (skipComments && temp instanceof Perl6Comment)))
            temp = toRight ? temp.getNextSibling() : temp.getPrevSibling();
        return temp;
    }

    @Nullable
    public static PsiElement getLastNonSpaceChild(@NotNull PsiElement node) {
        PsiElement[] children = node.getChildren();
        if (children.length == 0)
            return null;
        return skipSpaces(children[children.length - 1], false);
    }

    public static void terminateStatement(PsiElement element) {
        if (!(element instanceof Perl6Statement))
            return;

        Perl6Statement statement = Perl6ElementFactory.createStatementFromText(element.getProject(), element.getText() + ";");
        element.replace(statement);
    }

    public static void deleteElementDocComments(@Nullable PsiElement decl) {
        if (decl == null)
            return;
        PsiElement removeStart = null;
        PsiElement removeEnd = null;

        PsiElement prevSibling = decl.getPrevSibling();
        PsiElement preComment = skipSpaces(prevSibling, false, false);
        while (preComment instanceof PodPreComment) {
            removeStart = preComment;
            preComment = skipSpaces(preComment.getPrevSibling(), false, false);
        }

        PsiElement nextSibling = decl.getNextSibling();
        PsiElement postComment = skipSpaces(nextSibling, true, false);
        while (postComment instanceof PodPostComment) {
            removeEnd = postComment;
            postComment = skipSpaces(postComment.getNextSibling(), true, false);
        }
        if (removeStart != null)
            decl.getParent().deleteChildRange(removeStart, decl.getPrevSibling());
        if (removeEnd != null)
            decl.getParent().deleteChildRange(decl.getNextSibling(), removeEnd);
    }
}
