package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static edument.perl6idea.parsing.Perl6TokenTypes.PARAMETER_SEPARATOR;

public interface Perl6Documented {
    @Nullable
    PsiElement NEWLINE_COMMENT_ELEMENT = null;

    @NotNull
    default List<PsiElement> getDocBlocks() {
        if (!(this instanceof PsiElement))
            return Collections.emptyList();
        List<PsiElement> result = new ArrayList<>();

        Perl6PsiElement nodeToGatherComments =
            PsiTreeUtil.getNonStrictParentOfType((PsiElement)this, Perl6Statement.class, Perl6Parameter.class);
        if (nodeToGatherComments == null) return Collections.emptyList();
        PsiElement temp = nodeToGatherComments.getPrevSibling();
        gatherInlineComments(temp, false, result);
        if (result.size() != 0)
            result.remove(result.size() - 1);
        PsiElement maybeSeparator = Perl6PsiUtil.skipSpaces(nodeToGatherComments.getNextSibling(), true);
        boolean shouldVisitNodeChildren = nodeToGatherComments instanceof Perl6Parameter &&
                                          maybeSeparator != null && maybeSeparator.getNode().getElementType() != PARAMETER_SEPARATOR;
        temp = shouldVisitNodeChildren ?
               nodeToGatherComments.getFirstChild() :
               nodeToGatherComments.getNextSibling();
        while (temp != null && !(temp instanceof PodPostComment))
            temp = temp.getNextSibling();
        if (temp != null)
            gatherInlineComments(temp, true, result);
        return result;
    }

    @Nullable
    default String getDocsString() {
        List<PsiElement> blocks = getDocBlocks();
        StringBuilder builder = new StringBuilder();
        for (PsiElement block : blocks) {
            if (block == NEWLINE_COMMENT_ELEMENT)
                builder.append("\n");
            else
                builder.append(block.getText().trim());
        }
        return builder.toString().trim().replace("\n", "<br>");
    }

    static void gatherInlineComments(PsiElement temp,
                                     boolean toRight,
                                     List<PsiElement> elements) {
        Class<?> clazz = toRight ? PodPostComment.class : PodPreComment.class;
        while (true) {
            if (toRight)
                elements.add(NEWLINE_COMMENT_ELEMENT);
            if (!toRight)
                elements.add(0, NEWLINE_COMMENT_ELEMENT);
            temp = Perl6PsiUtil.skipSpaces(temp, toRight);
            if (temp == null) break;

            if (clazz.isInstance(temp)) {
                PsiElement commentContent = toRight ? temp.getFirstChild() : temp.getLastChild();
                for (PsiElement comment = commentContent;
                     comment != null;
                     comment = Perl6PsiUtil.skipSpaces(toRight ? comment.getNextSibling() : comment.getPrevSibling(), toRight)) {
                    if (comment.getNode().getElementType() != Perl6TokenTypes.COMMENT)
                        continue;
                    if (toRight) {
                        if (comment.getText().startsWith("\n"))
                            elements.add(NEWLINE_COMMENT_ELEMENT);
                        elements.add(comment);
                    } else {
                        elements.add(0, comment);
                        if (comment.getText().startsWith("\n"))
                            elements.add(0, NEWLINE_COMMENT_ELEMENT);
                    }
                }
                temp = Perl6PsiUtil.skipSpaces(toRight ? temp.getNextSibling() : temp.getPrevSibling(), toRight);
                continue;
            }
            break;
        }
    }
}
