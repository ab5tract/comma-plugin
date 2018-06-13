package edument.perl6idea.editor;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import edument.perl6idea.parsing.Perl6TokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Perl6BraceMatcher implements PairedBraceMatcher {
    @NotNull
    @Override
    public BracePair[] getPairs() {
        return new BracePair[]{
                new BracePair(Perl6TokenTypes.BLOCK_CURLY_BRACKET_OPEN, Perl6TokenTypes.BLOCK_CURLY_BRACKET_CLOSE, true),
                new BracePair(Perl6TokenTypes.STRING_LITERAL_QUOTE_OPEN, Perl6TokenTypes.STRING_LITERAL_QUOTE_CLOSE, false),
                new BracePair(Perl6TokenTypes.PARENTHESES_OPEN, Perl6TokenTypes.PARENTHESES_CLOSE, false),
                new BracePair(Perl6TokenTypes.SIGNATURE_BRACKET_OPEN, Perl6TokenTypes.SIGNATURE_BRACKET_CLOSE, false),
                new BracePair(Perl6TokenTypes.TYPE_COERCION_PARENTHESES_OPEN, Perl6TokenTypes.TYPE_COERCION_PARENTHESES_CLOSE, false),
                new BracePair(Perl6TokenTypes.REGEX_CAPTURE_PARENTHESES_OPEN, Perl6TokenTypes.REGEX_CAPTURE_PARENTHESES_CLOSE, false),
                new BracePair(Perl6TokenTypes.ARRAY_INDEX_BRACKET_OPEN, Perl6TokenTypes.ARRAY_INDEX_BRACKET_CLOSE, false),
                new BracePair(Perl6TokenTypes.HASH_INDEX_BRACKET_OPEN, Perl6TokenTypes.HASH_INDEX_BRACKET_CLOSE, false),
                new BracePair(Perl6TokenTypes.ARRAY_COMPOSER_OPEN, Perl6TokenTypes.ARRAY_COMPOSER_CLOSE, false),
                new BracePair(Perl6TokenTypes.REGEX_GROUP_BRACKET_OPEN, Perl6TokenTypes.REGEX_GROUP_BRACKET_CLOSE, false),
                new BracePair(Perl6TokenTypes.REGEX_ASSERTION_ANGLE_OPEN, Perl6TokenTypes.REGEX_ASSERTION_ANGLE_CLOSE, false)
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
