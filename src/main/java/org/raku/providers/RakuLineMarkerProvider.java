package org.raku.providers;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.navigation.GotoRelatedItem;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.NotNullFunction;
import com.intellij.util.containers.ContainerUtil;
import org.raku.RakuIcons;
import org.raku.parsing.RakuTokenTypes;
import org.raku.psi.RakuPackageDecl;
import org.raku.psi.RakuTrait;
import org.raku.psi.external.RakuExternalPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@InternalIgnoreDependencyViolation
public class RakuLineMarkerProvider extends RelatedItemLineMarkerProvider {
    private static final NotNullFunction<PsiElement, Collection<? extends GotoRelatedItem>> PERL6_GOTO_RELATED_ITEM_PROVIDER =
        dom -> Collections.singletonList(new GotoRelatedItem(dom, "Raku"));

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        RakuPackageDecl decl = isValidNavigationElement(element);
        if (decl == null) return;

        List<PsiElement> targets = new ArrayList<>();

        for (RakuPackageDecl parent : decl.collectParents()) {
            if (parent instanceof RakuExternalPsiElement)
                continue;
            targets.add(parent);
        }
        for (RakuPackageDecl child : decl.collectChildren()) {
            if (child instanceof RakuExternalPsiElement)
                continue;
            targets.add(child);
        }

        if (targets.size() > 0)
            result.add(NavigationGutterIconBuilder
                           .create(RakuIcons.CLASS, ContainerUtil::createMaybeSingletonList, PERL6_GOTO_RELATED_ITEM_PROVIDER)
                           .setTargets(targets)
                           .setTooltipText("Navigate to subtypes and supertypes")
                           .createLineMarkerInfo(element)
            );
    }

    @Nullable
    private static RakuPackageDecl isValidNavigationElement(@NotNull PsiElement element) {
        // If it is not a type name, we don't annotate it
        if (element.getNode().getElementType() != RakuTokenTypes.PACKAGE_NAME)
            return null;
        // If it is a type name inside of trait, we don't annotate it
        if (PsiTreeUtil.getParentOfType(element, RakuTrait.class) != null)
            return null;
        // Return an outer package for the type name we are working with
        return PsiTreeUtil.getParentOfType(element, RakuPackageDecl.class);
    }
}
