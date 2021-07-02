package edument.perl6idea.cro.template.editor;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CroTemplateBraceMatcher implements PairedBraceMatcher {
    @Override
    public BracePair @NotNull [] getPairs() {
        return new BracePair[] {
                new BracePair(CroTemplateTokenTypes.OPEN_PAREN, CroTemplateTokenTypes.CLOSE_PAREN, false),
                new BracePair(CroTemplateTokenTypes.OPEN_BRACKET, CroTemplateTokenTypes.CLOSE_BRACKET, false),
                new BracePair(CroTemplateTokenTypes.OPEN_CURLY, CroTemplateTokenTypes.CLOSE_CURLY, false),
                new BracePair(CroTemplateTokenTypes.OPEN_ANGLE, CroTemplateTokenTypes.CLOSE_ANGLE, false),
                new BracePair(CroTemplateTokenTypes.TEMPLATE_TAG_OPEN, CroTemplateTokenTypes.TEMPLATE_TAG_CLOSE, false),
                new BracePair(CroTemplateTokenTypes.LITERAL_TAG_OPEN, CroTemplateTokenTypes.LITERAL_TAG_CLOSE, false),
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
