package org.raku.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import org.raku.parsing.RakuTokenTypes;
import org.raku.psi.*;
import org.raku.psi.stub.RakuScopedDeclStub;
import org.jetbrains.annotations.NotNull;

public class RakuScopedDeclImpl extends StubBasedPsiElementBase<RakuScopedDeclStub> implements RakuScopedDecl {
    public RakuScopedDeclImpl(@NotNull ASTNode node) {
        super(node);
    }

    public RakuScopedDeclImpl(final RakuScopedDeclStub stub, final IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String getScope() {
        RakuScopedDeclStub stub = this.getStub();
        if (stub != null)
            return stub.getScope();

        ASTNode declarator = getNode().findChildByType(RakuTokenTypes.SCOPE_DECLARATOR);
        return declarator != null ? declarator.getText() : "";
    }

    public String toString() {
        return getClass().getSimpleName() + "(Raku:SCOPED_DECLARATION)";
    }
}
