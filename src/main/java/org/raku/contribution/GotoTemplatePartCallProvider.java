package org.raku.contribution;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.navigation.GotoRelatedProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.containers.ContainerUtil;
import org.raku.cro.CroTemplateIndex;
import org.raku.cro.template.psi.CroTemplatePart;
import org.raku.psi.RakuSubCall;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.raku.cro.template.parsing.CroTemplateTokenTypes.PART_NAME;

public class GotoTemplatePartCallProvider extends GotoRelatedProvider {
    @Override
    public @NotNull List<? extends GotoRelatedItem> getItems(@NotNull PsiElement psiElement) {
        if (!(psiElement.getParent() instanceof CroTemplatePart) || psiElement.getNode().getElementType() != PART_NAME)
            return Collections.emptyList();
        String partName = psiElement.getText();

        var croTemplateIndex = CroTemplateIndex.getInstance();
        Collection<RakuSubCall> collection =
           StubIndex.getElements(croTemplateIndex.getKey(),
                                 partName,
                                 psiElement.getProject(),
                                 GlobalSearchScope.projectScope(psiElement.getProject()),
                                 RakuSubCall.class);
        return ContainerUtil.map(collection, GotoRelatedItem::new);
    }
}
