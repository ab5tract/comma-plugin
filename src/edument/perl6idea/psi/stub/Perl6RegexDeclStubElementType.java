package edument.perl6idea.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import edument.perl6idea.Perl6Language;
import edument.perl6idea.psi.Perl6RegexDecl;
import edument.perl6idea.psi.impl.Perl6RegexDeclImpl;
import edument.perl6idea.psi.stub.impl.Perl6RegexDeclStubImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Perl6RegexDeclStubElementType extends IStubElementType<Perl6RegexDeclStub, Perl6RegexDecl> {
    public Perl6RegexDeclStubElementType() {
        super("REGEX_DECLARATION", Perl6Language.INSTANCE);
    }

    @Override
    public Perl6RegexDecl createPsi(@NotNull Perl6RegexDeclStub stub) {
        return new Perl6RegexDeclImpl(stub, this);
    }

    @NotNull
    @Override
    public Perl6RegexDeclStub createStub(@NotNull Perl6RegexDecl psi, StubElement parentStub) {
        return new Perl6RegexDeclStubImpl(parentStub, psi.getRegexName());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "perl6.stub.regexDeclaration";
    }

    @Override
    public void serialize(@NotNull Perl6RegexDeclStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getRegexName());
    }

    @NotNull
    @Override
    public Perl6RegexDeclStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef regexNameRef = dataStream.readName();
        return new Perl6RegexDeclStubImpl(parentStub, regexNameRef.getString());
    }

    @Override
    public void indexStub(@NotNull Perl6RegexDeclStub stub, @NotNull IndexSink sink) {

    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return !((Perl6RegexDecl)node.getPsi()).getRegexName().equals("<anon>");
    }
}
