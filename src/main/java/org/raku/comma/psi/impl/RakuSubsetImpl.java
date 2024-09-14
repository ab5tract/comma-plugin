package org.raku.comma.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.raku.comma.pod.PodDomBuildingContext;
import org.raku.comma.pod.PodDomSubsetDeclarator;
import org.raku.comma.psi.*;
import org.raku.comma.psi.stub.RakuSubsetStub;
import org.raku.comma.psi.stub.RakuSubsetStubElementType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RakuSubsetImpl extends RakuTypeStubBasedPsi<RakuSubsetStub> implements RakuSubset {
    public RakuSubsetImpl(@NotNull ASTNode node) {
        super(node);
    }

    public RakuSubsetImpl(RakuSubsetStub stub, RakuSubsetStubElementType type) {
        super(stub, type);
    }

    @Override
    public String getSubsetName() {
        return getName();
    }

    @Override
    public RakuPackageDecl getSubsetBaseType() {
        RakuTrait trait = findBaseTypeTrait();
        if (trait == null) return null;
        RakuTypeName type = trait.getCompositionTypeName();
        if (type == null) return null;
        PsiReference ref = type.getReference();
        if (ref == null) return null;
        PsiElement resolved = ref.resolve();
        if (resolved instanceof RakuPackageDecl)
            return (RakuPackageDecl)resolved;
        return null;
    }

    @Override
    public String getSubsetBaseTypeName() {
        RakuTrait trait = findBaseTypeTrait();
        return trait == null ? "Any" : trait.getTraitName();
    }

    private RakuTrait findBaseTypeTrait() {
        List<RakuTrait> traits = getTraits();
        for (RakuTrait trait : traits) {
            if (!trait.getTraitModifier().equals("of")) continue;
            return trait;
        }
        return null;
    }

    public String toString() {
        return getClass().getSimpleName() + "(Raku:SUBSET)";
    }

    @Override
    public void collectPodAndDocumentables(PodDomBuildingContext context) {
        String name = getName();
        if (name != null && !name.isEmpty()) {
            String[] parts = name.split("::");
            String globalName = context.prependGlobalNameParts(name);
            boolean isLexical = !getScope().equals("our");
            RakuTrait exportTrait = findTrait("is", "export");
            boolean visible = !isLexical && globalName != null || exportTrait != null;
            if (visible) {
                String shortName = parts[parts.length - 1];
                String baseType = getSubsetBaseTypeName();
                context.addType(new PodDomSubsetDeclarator(getTextOffset(), shortName, globalName,
                        getDocBlocks(), exportTrait, baseType));
            }
        }
    }
}
