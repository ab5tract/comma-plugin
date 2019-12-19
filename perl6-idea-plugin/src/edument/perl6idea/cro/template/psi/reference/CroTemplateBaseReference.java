package edument.perl6idea.cro.template.psi.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CroTemplateBaseReference<T extends PsiElement> extends PsiReferenceBase<T> {
    public CroTemplateBaseReference(@NotNull T element, TextRange rangeInElement) {
        super(element, rangeInElement);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        String name = getValue();
        CroTemplateResolveCollector collector = new CroTemplateResolveCollector(name, getSymbolKind());
        CroTemplateScopeWalker.walkWithCollector(collector, getElement());
        return collector.getResolution();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        CroTemplateCompletionCollector collector = new CroTemplateCompletionCollector(getSymbolKind());
        CroTemplateScopeWalker.walkWithCollector(collector, getElement());
        return collector.getResolutions().toArray();
    }

    protected abstract CroTemplateSymbolKind getSymbolKind();
}
