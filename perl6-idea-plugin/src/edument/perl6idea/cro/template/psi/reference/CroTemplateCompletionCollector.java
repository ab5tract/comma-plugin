package edument.perl6idea.cro.template.psi.reference;

import com.intellij.psi.PsiElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CroTemplateCompletionCollector implements CroTemplateSymbolCollector {
    private final CroTemplateSymbolKind wantedKind;
    private final Map<String, PsiElement> seen = new HashMap<>();

    public CroTemplateCompletionCollector(CroTemplateSymbolKind wantedKind) {
        this.wantedKind = wantedKind;
    }

    @Override
    public void offer(String name, CroTemplateSymbolKind kind, PsiElement element) {
        if (kind == wantedKind)
            seen.putIfAbsent(name, element);
    }

    @Override
    public boolean isSatisfied() {
        return false;
    }

    public Collection<PsiElement> getResolutions() {
        return seen.values();
    }
}
