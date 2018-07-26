package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import edument.perl6idea.psi.Perl6TypeName;
import edument.perl6idea.psi.Perl6TypeNameReference;
import edument.perl6idea.psi.stub.Perl6TypeNameStub;
import edument.perl6idea.psi.stub.Perl6TypeNameStubElementType;
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
        return getText();
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:TYPE_NAME)";
    }
}