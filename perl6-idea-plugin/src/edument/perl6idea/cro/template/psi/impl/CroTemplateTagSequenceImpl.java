package edument.perl6idea.cro.template.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.cro.template.psi.CroTemplateTagSequence;
import edument.perl6idea.cro.template.psi.Declaration;
import edument.perl6idea.cro.template.psi.reference.CroTemplateSymbolCollector;
import org.jetbrains.annotations.NotNull;

public class CroTemplateTagSequenceImpl extends ASTWrapperPsiElement implements CroTemplateTagSequence {
    public CroTemplateTagSequenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void offerAllTo(CroTemplateSymbolCollector collector) {
        Declaration[] types = PsiTreeUtil.getChildrenOfType(this, Declaration.class);
        if (types == null)
            return;
        for (Declaration declaration : types) {
            declaration.declareToCollector(collector);
        }
    }
}
