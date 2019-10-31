package edument.perl6idea.cro.template.editor;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes;

public class CroTemplateQuoteHandler extends SimpleTokenSetQuoteHandler {
    public CroTemplateQuoteHandler() {
        super(TokenSet.create(
                CroTemplateTokenTypes.ATTRIBUTE_QUOTE,
                CroTemplateTokenTypes.STRING_QUOTE_SINGLE));
    }
}
