package edument.perl6idea.editor;

import com.intellij.lang.Commenter;
import com.intellij.lang.CustomUncommenter;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@InternalIgnoreDependencyViolation
public class Perl6Commenter implements Commenter, CustomUncommenter {
    @Nullable
    @Override
    public String getLineCommentPrefix() {
        return "#";
    }

    @Nullable
    @Override
    public String getBlockCommentPrefix() {
        return "#`[";
    }

    @Nullable
    @Override
    public String getBlockCommentSuffix() {
        return "]";
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentSuffix() {
        return null;
    }

    @Nullable
    @Override
    public TextRange findMaximumCommentedRange(@NotNull CharSequence text) {
        String charStream = text.toString().trim();
        if (charStream.startsWith("#`[") && charStream.endsWith("]"))
            return new TextRange(0, text.length() - 1);
        return null;
    }

    @NotNull
    @Override
    public Collection<? extends Couple<TextRange>> getCommentRangesToDelete(@NotNull CharSequence sequence) {
        List<Couple<TextRange>> commentRanges = new ArrayList<>();
        String text = sequence.toString();
        int start = text.indexOf("#`[");
        int end = text.lastIndexOf("]");
        commentRanges.add(new Couple<>(new TextRange(start, start + 4), new TextRange(end, end + 2)));
        return commentRanges;
    }
}
