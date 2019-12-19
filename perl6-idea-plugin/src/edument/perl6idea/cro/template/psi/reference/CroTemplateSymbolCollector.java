package edument.perl6idea.cro.template.psi.reference;

import com.intellij.psi.PsiElement;

public interface CroTemplateSymbolCollector {
    void offer(String name, CroTemplateSymbolKind kind, PsiElement element);
    boolean isSatisfied();
}
