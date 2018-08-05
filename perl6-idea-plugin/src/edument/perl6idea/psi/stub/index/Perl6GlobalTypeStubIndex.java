package edument.perl6idea.psi.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class Perl6GlobalTypeStubIndex extends StringStubIndexExtension<Perl6IndexableType> {
    private static final int INDEX_VERSION = 4;
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
    public StubIndexKey<String, Perl6IndexableType> getKey() {
        return Perl6StubIndexKeys.GLOBAL_TYPES;
    }
}
