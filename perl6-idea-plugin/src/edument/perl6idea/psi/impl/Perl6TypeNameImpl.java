package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6TypeNameStub;
import edument.perl6idea.psi.stub.Perl6TypeNameStubElementType;
import edument.perl6idea.psi.type.Perl6ResolvedType;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6UnresolvedType;
import org.jetbrains.annotations.NotNull;

public class Perl6TypeNameImpl extends StubBasedPsiElementBase<Perl6TypeNameStub> implements Perl6TypeName {
    public Perl6TypeNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6TypeNameImpl(Perl6TypeNameStub stub, Perl6TypeNameStubElementType type) {
        super(stub, type);
    }

    @Override
    public PsiReference getReference() {
        return new Perl6TypeNameReference(this);
    }

    @Override
    public String getTypeName() {
        Perl6TypeNameStub stub = getStub();
        if (stub != null)
            return stub.getTypeName();
        Perl6LongName longName = findChildByClass(Perl6LongName.class);
        assert longName != null; // We always parse one at the start of a type name
        return longName.getNameWithoutColonPairs();
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:TYPE_NAME)";
    }

    @Override
    public @NotNull Perl6Type inferType() {
        PsiElement resolution = getReference().resolve();
        return resolution instanceof Perl6PsiElement
               ? new Perl6ResolvedType(getTypeName(), (Perl6PsiElement)resolution)
               : new Perl6UnresolvedType(getTypeName());
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        Perl6LongName type = Perl6ElementFactory
            .createTypeName(getProject(), name);
        Perl6LongName longName = findChildByClass(Perl6LongName.class);
        if (longName != null) {
            ASTNode keyNode = longName.getNode();
            ASTNode newKeyNode = type.getNode();
            getNode().replaceChild(keyNode, newKeyNode);
        }
        return this;
    }
}
