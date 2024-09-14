package org.raku.editor;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.search.IndexPatternBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.raku.cro.template.parsing.CroTemplateLexer;
import org.raku.cro.template.parsing.CroTemplateTokenTypes;
import org.raku.cro.template.psi.CroTemplateFile;
import org.raku.parsing.RakuLexer;
import org.raku.parsing.RakuTokenTypes;
import org.raku.psi.RakuFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class RakuIndexPatternBuilder implements IndexPatternBuilder {
    public static final TokenSet RAKU_COMMENT_TOKEN_SET = TokenSet.create(RakuTokenTypes.COMMENT, CroTemplateTokenTypes.COMMENT);

    @Override
    public @Nullable Lexer getIndexingLexer(@NotNull PsiFile file) {
        if (file instanceof RakuFile) {
            return new RakuLexer();
        }
        else if (file instanceof CroTemplateFile) {
            return new CroTemplateLexer();
        }
        return null;
    }

    @Override
    public @Nullable TokenSet getCommentTokenSet(@NotNull PsiFile file) {
        if (file instanceof RakuFile || file instanceof CroTemplateFile) {
            return RAKU_COMMENT_TOKEN_SET;
        }
        return null;
    }

    @Override
    public int getCommentStartDelta(IElementType tokenType) {
        return 1;
    }

    @Override
    public int getCommentEndDelta(IElementType tokenType) {
        return tokenType == CroTemplateTokenTypes.COMMENT ? 3 : 0;
    }
}
