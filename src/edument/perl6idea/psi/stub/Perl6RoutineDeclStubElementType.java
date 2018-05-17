package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.impl.Perl6RoutineDeclImpl;
import edument.perl6idea.psi.stub.impl.Perl6RoutineDeclStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Perl6RoutineDeclStubElementType extends IStubElementType<Perl6RoutineDeclStub, Perl6RoutineDecl> {
    public Perl6RoutineDeclStubElementType() {
        super("ROUTINE_DECLARATION", Perl6Language.INSTANCE);
    }

    @Override
    public Perl6RoutineDecl createPsi(@NotNull Perl6RoutineDeclStub stub) {
        return new Perl6RoutineDeclImpl(stub, this);
    }

    @NotNull
    @Override
    public Perl6RoutineDeclStub createStub(@NotNull Perl6RoutineDecl psi, StubElement parentStub) {
        return new Perl6RoutineDeclStubImpl(parentStub, psi.getRoutineName(), psi.getRoutineKind());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.routineDeclaration";
    }

    @Override
    public void serialize(@NotNull Perl6RoutineDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getRoutineName());
        dataStream.writeName(stub.getRoutineKind());
    }

    @NotNull
    @Override
    public Perl6RoutineDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef routineNameRef = dataStream.readName();
        StringRef routineKindRef = dataStream.readName();
        return new Perl6RoutineDeclStubImpl(parentStub, routineNameRef.getString(), routineKindRef.getString());
    }

    @Override
    public void indexStub(@NotNull Perl6RoutineDeclStub stub, @NotNull IndexSink sink) {

    }
}
