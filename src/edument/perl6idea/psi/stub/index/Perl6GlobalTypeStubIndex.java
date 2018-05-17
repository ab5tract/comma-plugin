package edument.perl6idea.psi.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.psi.Perl6PackageDecl;
import org.jetbrains.annotations.NotNull;

public class Perl6GlobalTypeStubIndex extends StringStubIndexExtension<Perl6PackageDecl> {
    private static final int INDEX_VERSION = 3;
    private static final Perl6GlobalTypeStubIndex instance = new Perl6GlobalTypeStubIndex();

    public static Perl6GlobalTypeStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, Perl6PackageDecl> getKey() {
        return Perl6StubIndexKeys.GLOBAL_TYPES;
    }
}
