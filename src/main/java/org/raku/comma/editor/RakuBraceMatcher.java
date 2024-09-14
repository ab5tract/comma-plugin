package org.raku.comma.editor;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.raku.comma.parsing.RakuTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class RakuBraceMatcher implements PairedBraceMatcher {
    @Override
    public BracePair @NotNull [] getPairs() {
        return new BracePair[]{
                new BracePair(RakuTokenTypes.BLOCK_CURLY_BRACKET_OPEN, RakuTokenTypes.BLOCK_CURLY_BRACKET_CLOSE, true),
                new BracePair(RakuTokenTypes.STRING_LITERAL_QUOTE_OPEN, RakuTokenTypes.STRING_LITERAL_QUOTE_CLOSE, false),
                new BracePair(RakuTokenTypes.PARENTHESES_OPEN, RakuTokenTypes.PARENTHESES_CLOSE, false),
                new BracePair(RakuTokenTypes.CONTEXTUALIZER_OPEN, RakuTokenTypes.CONTEXTUALIZER_CLOSE, false),
                new BracePair(RakuTokenTypes.SIGNATURE_BRACKET_OPEN, RakuTokenTypes.SIGNATURE_BRACKET_CLOSE, false),
                new BracePair(RakuTokenTypes.TYPE_COERCION_PARENTHESES_OPEN, RakuTokenTypes.TYPE_COERCION_PARENTHESES_CLOSE, false),
                new BracePair(RakuTokenTypes.REGEX_CAPTURE_PARENTHESES_OPEN, RakuTokenTypes.REGEX_CAPTURE_PARENTHESES_CLOSE, false),
                new BracePair(RakuTokenTypes.ARRAY_INDEX_BRACKET_OPEN, RakuTokenTypes.ARRAY_INDEX_BRACKET_CLOSE, false),
                new BracePair(RakuTokenTypes.HASH_INDEX_BRACKET_OPEN, RakuTokenTypes.HASH_INDEX_BRACKET_CLOSE, false),
                new BracePair(RakuTokenTypes.ARRAY_COMPOSER_OPEN, RakuTokenTypes.ARRAY_COMPOSER_CLOSE, false),
                new BracePair(RakuTokenTypes.REGEX_GROUP_BRACKET_OPEN, RakuTokenTypes.REGEX_GROUP_BRACKET_CLOSE, false),
                new BracePair(RakuTokenTypes.REGEX_ASSERTION_ANGLE_OPEN, RakuTokenTypes.REGEX_ASSERTION_ANGLE_CLOSE, false),
                new BracePair(RakuTokenTypes.POD_FORMAT_STARTER, RakuTokenTypes.POD_FORMAT_STOPPER, false),
                new BracePair(RakuTokenTypes.COMMENT_QUOTE_OPEN, RakuTokenTypes.COMMENT_QUOTE_CLOSE, false),
        };
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
