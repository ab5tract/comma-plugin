package edument.perl6idea.cro.template.psi.reference;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes;
import edument.perl6idea.cro.template.psi.CroTemplateVariableAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CroTemplateVariableReference extends PsiReferenceBase<CroTemplateVariableAccess> {
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

    @Nullable
    @Override
    public PsiElement resolve() {
        String name = getValue();
        CroTemplateResolveCollector collector = new CroTemplateResolveCollector(name);
        CroTemplateScopeWalker.walkWithCollector(collector, getElement());
        return collector.getResolution();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        CroTemplateCompletionCollector collector = new CroTemplateCompletionCollector();
        CroTemplateScopeWalker.walkWithCollector(collector, getElement());
        return collector.getResolutions().toArray();
    }
}
