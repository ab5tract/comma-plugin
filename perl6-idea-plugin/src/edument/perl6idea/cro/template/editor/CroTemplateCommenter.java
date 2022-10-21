package edument.perl6idea.cro.template.editor;

import com.intellij.lang.Commenter;
import com.intellij.lang.CustomUncommenter;
import com.intellij.openapi.util.Couple;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CroTemplateCommenter implements Commenter, CustomUncommenter {
    @Override
    public @Nullable String getLineCommentPrefix() {
        return null;
    }

    @Override
    public @Nullable String getBlockCommentPrefix() {
        return "<#>";
    }

    @Override
    public @Nullable String getBlockCommentSuffix() {
        return "</#>";
    }

    @Override
    public @Nullable String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Override
    public @Nullable String getCommentedBlockCommentSuffix() {
        return null;
    }

    @Override
    public boolean blockCommentRequiresFullLineSelection() {
        return false;
    }

    @Override
    public @Nullable TextRange findMaximumCommentedRange(@NotNull CharSequence text) {
        String charStream = text.toString().trim();
        if (charStream.startsWith("<#>") && charStream.endsWith("</#>"))
            return new TextRange(0, text.length());
        return null;
    }

    @Override
    public @NotNull Collection<? extends Couple<TextRange>> getCommentRangesToDelete(@NotNull CharSequence sequence) {
        List<Couple<TextRange>> commentRanges = new ArrayList<>();
        String text = sequence.toString();
        int start = text.indexOf("<#>");
        int end = text.lastIndexOf("</#>");
        commentRanges.add(new Couple<>(new TextRange(start, start + 3), new TextRange(end, end + 4)));
        return commentRanges;
    }
}
