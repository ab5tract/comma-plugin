package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes;
import edument.perl6idea.cro.template.psi.CroTemplateStringLiteral;
import org.jetbrains.annotations.NotNull;

public class CroTemplateStringLiteralImpl extends ASTWrapperPsiElement implements CroTemplateStringLiteral {
    public CroTemplateStringLiteralImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getStringValue() {
        ASTNode text = getNode().findChildByType(CroTemplateTokenTypes.STRING_TEXT);
        return text != null ? text.getText() : "";
    }
}
