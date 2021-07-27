package edument.perl6idea.contribution;

import com.intellij.navigation.GotoRelatedItem;
import com.intellij.navigation.GotoRelatedProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.cro.CroTemplateIndex;
import edument.perl6idea.cro.template.psi.CroTemplatePart;
import edument.perl6idea.psi.Perl6SubCall;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static edument.perl6idea.cro.template.parsing.CroTemplateTokenTypes.PART_NAME;

public class GotoTemplatePartCallProvider extends GotoRelatedProvider {
    @Override
    public @NotNull List<? extends GotoRelatedItem> getItems(@NotNull PsiElement psiElement) {
        if (!(psiElement.getParent() instanceof CroTemplatePart) || psiElement.getNode().getElementType() != PART_NAME)
            return Collections.emptyList();
        String partName = psiElement.getText();

        Collection<Perl6SubCall> collection =
            CroTemplateIndex.getInstance().get(partName, psiElement.getProject(), GlobalSearchScope.projectScope(psiElement.getProject()));
        return ContainerUtil.map(collection, s -> new GotoRelatedItem(s));
    }
}
