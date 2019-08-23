package edument.perl6idea.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import com.intellij.util.ArrayUtil;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6VariableDecl;
import edument.perl6idea.psi.impl.Perl6VariableDeclImpl;
import edument.perl6idea.psi.stub.impl.Perl6VariableDeclStubImpl;
import edument.perl6idea.psi.stub.index.Perl6StubIndexKeys;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Perl6VariableDeclStubElementType extends IStubElementType<Perl6VariableDeclStub, Perl6VariableDecl> {
    public Perl6VariableDeclStubElementType() {
        super("VARIABLE_DECLARATION", Perl6Language.INSTANCE);
    }

    @Override
    public Perl6VariableDecl createPsi(@NotNull Perl6VariableDeclStub stub) {
        return new Perl6VariableDeclImpl(stub, this);
    }

    @NotNull
    @Override
    public Perl6VariableDeclStub createStub(@NotNull Perl6VariableDecl psi, StubElement parentStub) {
        return new Perl6VariableDeclStubImpl(parentStub, psi.getVariableNames(), psi.inferType(), psi.isExported());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.variableDeclaration";
    }

    @Override
    public void serialize(@NotNull Perl6VariableDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        // We might have an arbitrary number of names declared, so save a counter too
        String[] names = stub.getVariableNames();
        dataStream.writeInt(names.length);
        for (String name : names)
            dataStream.writeName(name);
        dataStream.writeName(stub.getVariableType());
        dataStream.writeBoolean(stub.isExported());
    }

    @NotNull
    @Override
    public Perl6VariableDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        int numberOfNames = dataStream.readInt();
        List<String> names = new ArrayList<>();
        for (int i = 0; i < numberOfNames; i++)
            names.add(dataStream.readName().getString());
        StringRef variableTypeRef = dataStream.readName();
        String type = variableTypeRef == null ? null : variableTypeRef.getString();
        boolean exported = dataStream.readBoolean();
        return new Perl6VariableDeclStubImpl(parentStub, ArrayUtil.toStringArray(names), type, exported);
    }

    @Override
    public void indexStub(@NotNull Perl6VariableDeclStub stub, @NotNull IndexSink sink) {
        for (String name : stub.getVariableNames()) {
            sink.occurrence(Perl6StubIndexKeys.ALL_ATTRIBUTES, name);
        }
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        Perl6VariableDecl variableDecl = (Perl6VariableDecl) node.getPsi();
        String scope = variableDecl.getScope();
        return scope.equals("has") ||
                scope.equals("our") && variableDecl.isExported();
    }
}
