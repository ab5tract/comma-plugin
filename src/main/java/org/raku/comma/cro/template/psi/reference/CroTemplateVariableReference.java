package org.raku.comma.cro.template.psi.reference;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.TokenSet;
import org.raku.comma.cro.template.parsing.CroTemplateTokenTypes;
import org.raku.comma.cro.template.psi.CroTemplateVariableAccess;
import org.jetbrains.annotations.NotNull;

public class CroTemplateVariableReference extends CroTemplateBaseReference<CroTemplateVariableAccess> {
    public CroTemplateVariableReference(@NotNull CroTemplateVariableAccess element) {
        super(element, variableTextRange(element));
    }

    private static TextRange variableTextRange(CroTemplateVariableAccess element) {
        ASTNode[] variableName = element.getNode().getChildren(TokenSet.create(CroTemplateTokenTypes.VARIABLE_NAME));
        if (variableName.length == 0) {
            return new TextRange(0, element.getTextLength());
        }
        else {
            int startOffset = variableName[0].getStartOffset() - element.getTextOffset();
            return new TextRange(startOffset, startOffset + variableName[0].getTextLength());
        }
    }

    @Override
    protected CroTemplateSymbolKind getSymbolKind() {
        return CroTemplateSymbolKind.Variable;
    }
}
