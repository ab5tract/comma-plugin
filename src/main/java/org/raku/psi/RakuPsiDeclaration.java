package org.raku.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface RakuPsiDeclaration extends RakuPsiElement, PsiNameIdentifierOwner, RakuDocumented {
    @NotNull
    String getScope();

    @NotNull
    default List<RakuTrait> getTraits() {
        List<RakuTrait> traits = PsiTreeUtil.getChildrenOfTypeAsList(this, RakuTrait.class);
        RakuStatementList list = PsiTreeUtil.findChildOfType(this, RakuStatementList.class);
        if (list == null) return traits;

        // If empty list was returned, it is immutable,
        // so we need to make it mutable before checking other statements
        if (traits.size() == 0)
            traits = new ArrayList<>();

        for (PsiElement statement : list.getChildren()) {
            if (!(statement instanceof RakuStatement)) continue;
            PsiElement statementFirstChild = statement.getFirstChild();
            if (statementFirstChild instanceof RakuAlso) {
                RakuTrait trait = ((RakuAlso)statementFirstChild).getTrait();
                if (trait != null)
                    traits.add(trait);
            }
        }
        return traits;
    }

    @Nullable
    default RakuTrait findTrait(String mod, String name) {
        for (RakuTrait trait : getTraits())
            if (mod.equals(trait.getTraitModifier()) &&
                name.equals(trait.getTraitName()))
                return trait;
        return null;
    }

    default boolean isExported() {
        return findTrait("is", "export") != null ||
               !getScope().equals("my");
    }

    default String getCutName(String text) {
        // Chop off possible parenthesis or smileys
        int index = text.indexOf('(');
        if (index != -1)
            text = text.substring(0, index);
        index = text.lastIndexOf(":");
        if (index != -1 && text.charAt(index - 1) != ':')
            text = text.substring(0, index);
        return text;
    }

    default String getGlobalName() {
        // If it's not an our-scoped thing, no global name.
        String scope = getScope();
        if (!(scope.equals("our") || scope.equals("unit")))
            return null;

        // Walk up any enclosing packages.
        String name = getName();
        if (name == null) {
            return null;
        }
        StringBuilder globalName = new StringBuilder(name);
        PsiElement outer = getParent();
        while (outer != null && !(outer instanceof RakuFile)) {
            if (outer instanceof RakuPackageDecl pkg) {
                if (pkg.getScope().equals("my"))
                    return null;
                globalName.insert(0, pkg.getName() + "::");
            }
            outer = outer.getParent();
        }
        return globalName.toString();
    }
}
