package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
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
        List<Perl6Trait> traits = getTraits();
        for (Perl6Trait trait : traits) {
            if (!trait.getTraitModifier().equals("of")) continue;
            Perl6TypeName type = trait.getCompositionTypeName();
            if (type == null) break; // Not yet typed
            PsiReference ref = type.getReference();
            if (ref == null) break; // Some extreme error if ref is null, break
            PsiElement resolved = ref.resolve();
            if (resolved == null) break; // If external type, break
            return (Perl6PackageDecl)resolved;
        }
        return null;
    }

    @Override
    public String getSubsetBaseTypeName() {
        List<Perl6Trait> traits = getTraits();
        for (Perl6Trait trait : traits) {
            if (!trait.getTraitModifier().equals("of")) continue;
            return trait.getTraitName();
        }
        return null;
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:SUBSET)";
    }
}
