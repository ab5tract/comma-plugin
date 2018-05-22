package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6UseStatement;
import edument.perl6idea.psi.impl.Perl6UseStatementImpl;
import edument.perl6idea.psi.stub.impl.Perl6UseStatementStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Perl6UseStatementStubElementType extends IStubElementType<Perl6UseStatementStub, Perl6UseStatement> {
    public Perl6UseStatementStubElementType() {
        super("USE_STATEMENT", Perl6Language.INSTANCE);
    }

    @Override
    public Perl6UseStatement createPsi(@NotNull Perl6UseStatementStub stub) {
        return new Perl6UseStatementImpl(stub, this);
    }

    @NotNull
    @Override
    public Perl6UseStatementStub createStub(@NotNull Perl6UseStatement psi, StubElement parentStub) {
        return new Perl6UseStatementStubImpl(parentStub, psi.getModuleName());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.useStatement";
    }

    @Override
    public void serialize(@NotNull Perl6UseStatementStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getModuleName());
    }

    @NotNull
    @Override
    public Perl6UseStatementStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef ref = dataStream.readName();
        return new Perl6UseStatementStubImpl(parentStub, ref == null ? null : ref.getString());
    }

    @Override
    public void indexStub(@NotNull Perl6UseStatementStub stub, @NotNull IndexSink sink) {
    }
}
