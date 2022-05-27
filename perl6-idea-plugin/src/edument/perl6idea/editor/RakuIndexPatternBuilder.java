package edument.perl6idea.editor;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.search.IndexPatternBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.cro.template.parsing.CroTemplateLexer;
import edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes;
import edument.perl6idea.cro.template.psi.CroTemplateFile;
import edument.perl6idea.parsing.Perl6Lexer;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@InternalIgnoreDependencyViolation
public class RakuIndexPatternBuilder implements IndexPatternBuilder {
    public static final TokenSet RAKU_COMMENT_TOKEN_SET = TokenSet.create(Perl6TokenTypes.COMMENT, CroTemplateTokenTypes.COMMENT);

    @Override
    public @Nullable Lexer getIndexingLexer(@NotNull PsiFile file) {
        if (file instanceof Perl6File) {
            return new Perl6Lexer();
        }
        else if (file instanceof CroTemplateFile) {
            return new CroTemplateLexer();
        }
        return null;
    }

    @Override
    public @Nullable TokenSet getCommentTokenSet(@NotNull PsiFile file) {
        if (file instanceof Perl6File || file instanceof CroTemplateFile) {
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
