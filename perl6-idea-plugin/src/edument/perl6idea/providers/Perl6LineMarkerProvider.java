package edument.perl6idea.providers;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6IsTraitName;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6Trait;
import edument.perl6idea.psi.Perl6TypeName;
import edument.perl6idea.psi.stub.index.Perl6GlobalTypeStubIndex;
import edument.perl6idea.psi.stub.index.Perl6IndexableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.codeInsight.navigation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Perl6LineMarkerProvider extends RelatedItemLineMarkerProvider {
    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
        Perl6PackageDecl decl = isValidNavigationElement(element);
        if (decl == null) return;

        List<PsiElement> targets = new ArrayList<>();

        collectParents(decl, targets);
        collectChildren(decl, targets);

        if (targets.size() > 0)
            result.add(NavigationGutterIconBuilder
                           .create(Perl6Icons.CLASS)
                           .setTargets(targets)
                           .setTooltipText("Navigate to parent classes")
                           .createLineMarkerInfo(element)
            );
    }

    private static void collectParents(Perl6PackageDecl decl, List<PsiElement> targets) {
        for (Perl6Trait trait : decl.getTraits()) {
            Perl6IsTraitName isTrait = PsiTreeUtil.findChildOfType(trait, Perl6IsTraitName.class);
            if (isTrait != null) {
                PsiElement resolved = isTrait.getReference().resolve();
                if (resolved != null)
                    targets.add(resolved);
            }
            else {
                Perl6TypeName doesTrait = PsiTreeUtil.findChildOfType(trait, Perl6TypeName.class);
                if (doesTrait != null) {
                    PsiElement resolved = doesTrait.getReference().resolve();
                    if (resolved != null)
                        targets.add(resolved);
                }
            }
        }
    }

    private static void collectChildren(Perl6PackageDecl decl, List<PsiElement> targets) {
        Perl6GlobalTypeStubIndex instance = Perl6GlobalTypeStubIndex.getInstance();
        Project project = decl.getProject();
        String name = decl.getPackageName();
        Collection<String> keys = instance.getAllKeys(project);
        for (String key : keys) {
            Collection<Perl6IndexableType> psi = instance.get(key, project, GlobalSearchScope.allScope(project));
            if (psi.size() == 1) {
                for (Perl6IndexableType type : psi) {
                    if (!(type instanceof Perl6PackageDecl))
                        continue;
                    Perl6Trait childTrait = ((Perl6PackageDecl)type).findTrait("does", name);
                    if (childTrait != null)
                        targets.add(type);
                    childTrait = ((Perl6PackageDecl)type).findTrait("is", name);
                    if (childTrait != null)
                        targets.add(type);
                }
            }
        }
    }

    @Nullable
    private static Perl6PackageDecl isValidNavigationElement(@NotNull PsiElement element) {
        // If it is not a type name, we don't annotate it
        if (element.getNode().getElementType() != Perl6TokenTypes.NAME)
            return null;
        // If it is a type name inside of trait, we don't annotate it
        if (PsiTreeUtil.getParentOfType(element, Perl6Trait.class) != null)
            return null;
        // Return an outer package for the type name we are working with
        PsiElement nameParent = element.getParent();
        return nameParent instanceof Perl6PackageDecl ? (Perl6PackageDecl)nameParent : null;
    }
}
