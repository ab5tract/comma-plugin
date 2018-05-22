package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6NeedStatement;
import edument.perl6idea.psi.impl.Perl6NeedStatementImpl;
import edument.perl6idea.psi.stub.impl.Perl6NeedStatementStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Perl6NeedStatementStubElementType extends IStubElementType<Perl6NeedStatementStub, Perl6NeedStatement> {
    public Perl6NeedStatementStubElementType() {
        super("NEED_STATEMENT", Perl6Language.INSTANCE);
    }

    @Override
    public Perl6NeedStatement createPsi(@NotNull Perl6NeedStatementStub stub) {
        return new Perl6NeedStatementImpl(stub, this);
    }

    @NotNull
    @Override
    public Perl6NeedStatementStub createStub(@NotNull Perl6NeedStatement psi, StubElement parentStub) {
        return new Perl6NeedStatementStubImpl(parentStub, psi.getModuleNames());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.needStatement";
    }

    @Override
    public void serialize(@NotNull Perl6NeedStatementStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        List<String> names = stub.getModuleNames();
        dataStream.writeInt(names.size());
        for (String name : names)
            dataStream.writeName(name);
    }

    @NotNull
    @Override
    public Perl6NeedStatementStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        int elems = dataStream.readInt();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < elems; i++) {
            StringRef ref = dataStream.readName();
            names.add(ref == null ? null : ref.getString());
        }
        return new Perl6NeedStatementStubImpl(null, names);
    }

    @Override
    public void indexStub(@NotNull Perl6NeedStatementStub stub, @NotNull IndexSink sink) {
    }
}
