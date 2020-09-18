package edument.perl6idea.cro.template.psi.reference;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes;
import edument.perl6idea.cro.template.psi.CroTemplateApply;
import org.jetbrains.annotations.NotNull;

public class CroTemplateApplyReference extends CroTemplateBaseReference<CroTemplateApply> {
    public CroTemplateApplyReference(@NotNull CroTemplateApply element) {
        super(element, macroNameTextRange(element));
    }

    private static TextRange macroNameTextRange(CroTemplateApply element) {
        ASTNode[] macroName = element.getNode().getChildren(TokenSet.create(CroTemplateTokenTypes.MACRO_NAME));
        if (macroName.length == 0) {
            return new TextRange(0, element.getTextLength());
        }
        else {
            int startOffset = macroName[0].getStartOffset() - element.getTextOffset();
            return new TextRange(startOffset, startOffset + macroName[0].getTextLength());
        }
    }

    @Override
    protected CroTemplateSymbolKind getSymbolKind() {
        return CroTemplateSymbolKind.Macro;
    }
}
