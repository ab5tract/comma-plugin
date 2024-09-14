package org.raku.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.io.StringRef;
import org.raku.RakuLanguage;
import org.raku.psi.RakuPsiDeclaration;
import org.raku.psi.RakuScopedDecl;
import org.raku.psi.RakuVariableDecl;
import org.raku.psi.impl.RakuScopedDeclImpl;
import org.raku.psi.stub.impl.RakuScopedDeclStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RakuScopedDeclStubElementType extends IStubElementType<RakuScopedDeclStub, RakuScopedDecl> {
    public RakuScopedDeclStubElementType() {
        super("SCOPED_DECLARATION", RakuLanguage.getInstance());
    }

    @Override
    public RakuScopedDecl createPsi(@NotNull RakuScopedDeclStub stub) {
        return new RakuScopedDeclImpl(stub, this);
    }

    @NotNull
    @Override
    public RakuScopedDeclStub createStub(@NotNull RakuScopedDecl psi, StubElement parentStub) {
        return new RakuScopedDeclStubImpl(parentStub, psi.getScope());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "raku.stub.scopedDecl";
    }

    @Override
    public void serialize(@NotNull RakuScopedDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getScope());
    }

    @NotNull
    @Override
    public RakuScopedDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef scope = dataStream.readName();
        return new RakuScopedDeclStubImpl(parentStub, scope == null ? null : scope.getString());
    }

    @Override
    public void indexStub(@NotNull RakuScopedDeclStub stub, @NotNull IndexSink sink) {
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        PsiElement element = node.getPsi();
        // Scope is either `has` for attribute or `our`, but with `is export` trait
        if (!(element instanceof RakuScopedDecl scopedDecl)) return false;

        if (scopedDecl.getScope().equals("has")) return true;
        if (!scopedDecl.getScope().equals("our")) return false;
        RakuPsiDeclaration childDeclaration = PsiTreeUtil.getChildOfType(scopedDecl, RakuPsiDeclaration.class);
        return childDeclaration instanceof RakuVariableDecl && childDeclaration.isExported();
    }
}
