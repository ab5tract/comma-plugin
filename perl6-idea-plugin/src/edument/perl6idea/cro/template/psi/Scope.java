package edument.perl6idea.cro.template.psi;

import com.intellij.psi.PsiElement;
import edument.perl6idea.cro.template.psi.reference.CroTemplateSymbolCollector;

public interface Scope extends PsiElement {
    void offerAllTo(CroTemplateSymbolCollector collector);
}
