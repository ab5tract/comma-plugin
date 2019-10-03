package edument.perl6idea.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.utils.Perl6PsiUtil;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6ElementTypes.COMMENT;

public interface Perl6Documented {
    @Nullable
    default String getDocsString() {
        if (!(this instanceof PsiElement))
            return null;

        StringBuilder builder = new StringBuilder();
        Perl6Statement statement = PsiTreeUtil.getParentOfType((PsiElement)this, Perl6Statement.class);
        if (statement == null) return null;
        PsiElement temp = statement.getPrevSibling();
        gatherInlineComments(temp, false, builder);
        builder.append("\n");
        temp = statement.getNextSibling();
        gatherInlineComments(temp, true, builder);
        return builder.toString().trim();
    }

    static void gatherInlineComments(PsiElement temp,
                                     boolean toRight,
                                     StringBuilder builder) {
        Class<?> clazz = toRight ? PodPostComment.class : PodPreComment.class;
        while (true) {
            temp = Perl6PsiUtil.skipSpaces(temp, toRight);
            if (temp == null) break;

            if (temp.getClass().isAssignableFrom(clazz)) {
                PsiElement commentContent = toRight ? temp.getFirstChild() : temp.getLastChild();
                for (PsiElement comment = commentContent; comment != null; comment = Perl6PsiUtil.skipSpaces(comment, toRight)) {
                    if (comment.getNode().getElementType() != COMMENT)
                        continue;

                    String text = comment.getText();
                    if (text.startsWith("\n"))
                        text = "\n" + text.trim();
                    else
                        text = text.trim();

                    if (toRight)
                        builder.append(text);
                    else
                        builder.insert(0, text);
                }
                if (toRight)
                    builder.append("\n");
                else
                    builder.insert(0, "\n");
                temp = Perl6PsiUtil.skipSpaces(temp, toRight);
                continue;
            }
            break;
        }
    }
}
