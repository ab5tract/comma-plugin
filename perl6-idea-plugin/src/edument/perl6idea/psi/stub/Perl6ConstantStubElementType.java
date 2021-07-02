package edument.perl6idea.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6Constant;
import edument.perl6idea.psi.impl.Perl6ConstantImpl;
import edument.perl6idea.psi.stub.impl.Perl6ConstantStubImpl;
import edument.perl6idea.psi.stub.index.Perl6StubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Perl6ConstantStubElementType extends IStubElementType<Perl6ConstantStub, Perl6Constant> {
    public Perl6ConstantStubElementType() {
        super("CONSTANT", Perl6Language.INSTANCE);
    }

    @Override
    public Perl6Constant createPsi(@NotNull Perl6ConstantStub stub) {
        return new Perl6ConstantImpl(stub, this);
    }

    @NotNull
    @Override
    public Perl6ConstantStub createStub(@NotNull Perl6Constant psi, StubElement parentStub) {
        return new Perl6ConstantStubImpl(parentStub, psi.getConstantName(), psi.isExported());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.constant";
    }

    @Override
    public void serialize(@NotNull Perl6ConstantStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getConstantName());
        dataStream.writeBoolean(stub.isExported());
    }

    @NotNull
    @Override
    public Perl6ConstantStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef constantNameRef = dataStream.readName();
        boolean isExport = dataStream.readBoolean();
        return new Perl6ConstantStubImpl(parentStub, constantNameRef == null ? null : constantNameRef.getString(), isExport);
    }

    @Override
    public void indexStub(@NotNull Perl6ConstantStub stub, @NotNull IndexSink sink) {
        sink.occurrence(Perl6StubIndexKeys.ALL_CONSTANTS, stub.getConstantName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return ((Perl6Constant)node.getPsi()).getConstantName() != null;
    }
}
