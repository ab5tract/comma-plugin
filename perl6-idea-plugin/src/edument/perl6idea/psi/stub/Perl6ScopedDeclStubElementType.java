package edument.perl6idea.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6PsiDeclaration;
import edument.perl6idea.psi.Perl6ScopedDecl;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.psi.impl.Perl6ScopedDeclImpl;
import edument.perl6idea.psi.stub.impl.Perl6ScopedDeclStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Perl6ScopedDeclStubElementType extends IStubElementType<Perl6ScopedDeclStub, Perl6ScopedDecl> {
    public Perl6ScopedDeclStubElementType() {
        super("SCOPED_DECLARATION", Perl6Language.INSTANCE);
    }

    @Override
    public Perl6ScopedDecl createPsi(@NotNull Perl6ScopedDeclStub stub) {
        return new Perl6ScopedDeclImpl(stub, this);
    }

    @NotNull
    @Override
    public Perl6ScopedDeclStub createStub(@NotNull Perl6ScopedDecl psi, StubElement parentStub) {
        return new Perl6ScopedDeclStubImpl(parentStub, psi.getScope());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.scopedDecl";
    }

    @Override
    public void serialize(@NotNull Perl6ScopedDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getScope());
    }

    @NotNull
    @Override
    public Perl6ScopedDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef scope = dataStream.readName();
        return new Perl6ScopedDeclStubImpl(parentStub, scope == null ? null : scope.getString());
    }

    @Override
    public void indexStub(@NotNull Perl6ScopedDeclStub stub, @NotNull IndexSink sink) {
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        PsiElement element = node.getPsi();
        // Scope is either `has` for attribute or `our`, but with `is export` trait
        if (!(element instanceof Perl6ScopedDecl)) return false;

        Perl6ScopedDecl scopedDecl = (Perl6ScopedDecl) element;
        if (scopedDecl.getScope().equals("has")) return true;
        if (!scopedDecl.getScope().equals("our")) return false;
        Perl6PsiDeclaration childDeclaration = PsiTreeUtil.getChildOfType(scopedDecl, Perl6PsiDeclaration.class);
        return childDeclaration instanceof Perl6VariableDecl && childDeclaration.isExported();
    }
}
