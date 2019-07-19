package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.Perl6ElementFactory;
import edument.perl6idea.psi.Perl6Trait;
import edument.perl6idea.psi.Perl6TypeName;
import edument.perl6idea.psi.stub.Perl6TraitStub;
import edument.perl6idea.psi.stub.Perl6TraitStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static edument.perl6idea.parsing.Perl6ElementTypes.IS_TRAIT_NAME;
import static edument.perl6idea.parsing.Perl6ElementTypes.TYPE_NAME;

public class Perl6TraitImpl extends StubBasedPsiElementBase<Perl6TraitStub> implements Perl6Trait {
    public Perl6TraitImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6TraitImpl(Perl6TraitStub stub, Perl6TraitStubElementType type) {
        super(stub, type);
    }

    @Override
    public String getTraitModifier() {
        Perl6TraitStub stub = getStub();
        if (stub != null)
            return stub.getTraitModifier();

        PsiElement trait = findChildByType(Perl6TokenTypes.TRAIT);
        return trait == null ? "" : trait.getText();
    }

    @Override
    public String getTraitName() {
        Perl6TraitStub stub = getStub();
        if (stub != null)
            return stub.getTraitName();

        PsiElement isName = findChildByType(IS_TRAIT_NAME);
        PsiElement typeName = findChildByType(TYPE_NAME);
        return isName != null ? isName.getText() :
               typeName != null ? typeName.getText() : "";
    }

    @Override
    public void changeTraitMod(String newMod) {
        replace(Perl6ElementFactory.createTrait(getProject(), newMod, getTraitName()));
    }

    @Nullable
    @Override
    public Perl6TypeName getCompositionTypeName() {
        return findChildByType(TYPE_NAME);
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:TRAIT)";
    }
}
