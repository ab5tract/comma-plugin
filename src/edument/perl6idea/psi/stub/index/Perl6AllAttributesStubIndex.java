package edument.perl6idea.psi.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.psi.Perl6VariableDecl;
import org.jetbrains.annotations.NotNull;

public class Perl6AllAttributesStubIndex extends StringStubIndexExtension<Perl6VariableDecl> {
    private static final int INDEX_VERSION = 3;
    private static final Perl6AllAttributesStubIndex instance = new Perl6AllAttributesStubIndex();

    public static Perl6AllAttributesStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, Perl6VariableDecl> getKey() {
        return Perl6StubIndexKeys.ALL_ATTRIBUTES;
    }
}
