package edument.perl6idea.cro.template.psi.reference;

import com.intellij.psi.PsiElement;

public class CroTemplateResolveCollector implements CroTemplateSymbolCollector {
    private final String wanted;
    private PsiElement found;

    public CroTemplateResolveCollector(String wanted) {
        this.wanted = wanted;
    }

    @Override
    public void offer(String name, PsiElement element) {
        if (found == null && wanted.equals(name))
            found = element;
    }

    @Override
    public boolean isSatisfied() {
        return found != null;
    }

    public PsiElement getResolution() {
        return found;
    }
}
