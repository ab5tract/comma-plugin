package org.raku.cro.template.psi.reference;

import com.intellij.psi.PsiElement;

public class CroTemplateResolveCollector implements CroTemplateSymbolCollector {
    private final String wantedName;
    private final CroTemplateSymbolKind wantedKind;
    private PsiElement found;

    public CroTemplateResolveCollector(String wantedName, CroTemplateSymbolKind wantedKind) {
        this.wantedName = wantedName;
        this.wantedKind = wantedKind;
    }

    @Override
    public void offer(String name, CroTemplateSymbolKind kind, PsiElement element) {
        if (found == null && kind == wantedKind && wantedName.equals(name))
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
