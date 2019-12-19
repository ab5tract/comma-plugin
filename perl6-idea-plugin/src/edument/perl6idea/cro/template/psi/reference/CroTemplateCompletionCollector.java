package edument.perl6idea.cro.template.psi.reference;

import com.intellij.psi.PsiElement;
import com.intellij.util.containers.hash.HashMap;

import java.util.Collection;
import java.util.Map;

public class CroTemplateCompletionCollector implements CroTemplateSymbolCollector {
    private Map<String, PsiElement> seen = new HashMap<>();

    @Override
    public void offer(String name, PsiElement element) {
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
