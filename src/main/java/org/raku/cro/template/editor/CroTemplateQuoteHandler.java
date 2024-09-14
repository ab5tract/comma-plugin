package org.raku.cro.template.editor;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.tree.TokenSet;
import org.raku.cro.template.parsing.CroTemplateTokenTypes;

@InternalIgnoreDependencyViolation
public class CroTemplateQuoteHandler extends SimpleTokenSetQuoteHandler {
    public CroTemplateQuoteHandler() {
        super(TokenSet.create(
                CroTemplateTokenTypes.ATTRIBUTE_QUOTE,
                CroTemplateTokenTypes.STRING_QUOTE_SINGLE));
    }
}
