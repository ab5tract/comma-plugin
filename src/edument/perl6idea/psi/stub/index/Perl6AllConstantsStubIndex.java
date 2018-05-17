package edument.perl6idea.psi.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.psi.Perl6Constant;
import org.jetbrains.annotations.NotNull;

public class Perl6AllConstantsStubIndex extends StringStubIndexExtension<Perl6Constant> {
    private static final int INDEX_VERSION = 3;
    private static final Perl6AllConstantsStubIndex instance = new Perl6AllConstantsStubIndex();

    public static Perl6AllConstantsStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, Perl6Constant> getKey() {
        return Perl6StubIndexKeys.ALL_CONSTANTS;
    }
}
