package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import edument.perl6idea.pod.PodDomBuildingContext;
import edument.perl6idea.pod.PodDomSubsetDeclarator;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6SubsetStub;
import edument.perl6idea.psi.stub.Perl6SubsetStubElementType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Perl6SubsetImpl extends Perl6TypeStubBasedPsi<Perl6SubsetStub> implements Perl6Subset {
    public Perl6SubsetImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6SubsetImpl(Perl6SubsetStub stub, Perl6SubsetStubElementType type) {
        super(stub, type);
    }

    @Override
    public String getSubsetName() {
        return getName();
    }

    @Override
    public Perl6PackageDecl getSubsetBaseType() {
        Perl6Trait trait = findBaseTypeTrait();
        if (trait == null) return null;
        Perl6TypeName type = trait.getCompositionTypeName();
        if (type == null) return null;
        PsiReference ref = type.getReference();
        if (ref == null) return null;
        PsiElement resolved = ref.resolve();
        if (resolved instanceof Perl6PackageDecl)
            return (Perl6PackageDecl)resolved;
        return null;
    }

    @Override
    public String getSubsetBaseTypeName() {
        Perl6Trait trait = findBaseTypeTrait();
        return trait == null ? "Any" : trait.getTraitName();
    }

    private Perl6Trait findBaseTypeTrait() {
        List<Perl6Trait> traits = getTraits();
        for (Perl6Trait trait : traits) {
            if (!trait.getTraitModifier().equals("of")) continue;
            return trait;
        }
        return null;
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:SUBSET)";
    }

    @Override
    public void collectPodAndDocumentables(PodDomBuildingContext context) {
        String name = getName();
        if (name != null && !name.isEmpty()) {
            String[] parts = name.split("::");
            String globalName = context.prependGlobalNameParts(name);
            boolean isLexical = !getScope().equals("our");
            Perl6Trait exportTrait = findTrait("is", "export");
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
