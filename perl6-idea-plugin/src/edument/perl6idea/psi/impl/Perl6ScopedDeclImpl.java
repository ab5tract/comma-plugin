package edument.perl6idea.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import edument.perl6idea.parsing.Perl6TokenTypes;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;
import org.jetbrains.annotations.NotNull;

public class Perl6ScopedDeclImpl extends StubBasedPsiElementBase<Perl6ScopedDeclStub> implements Perl6ScopedDecl {
    public Perl6ScopedDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6ScopedDeclImpl(final Perl6ScopedDeclStub stub, final IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String getScope() {
        Perl6ScopedDeclStub stub = this.getStub();
        if (stub != null)
            return stub.getScope();

        ASTNode declarator = getNode().findChildByType(Perl6TokenTypes.SCOPE_DECLARATOR);
        return declarator != null ? declarator.getText() : "";
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:SCOPED_DECLARATION)";
    }
}
