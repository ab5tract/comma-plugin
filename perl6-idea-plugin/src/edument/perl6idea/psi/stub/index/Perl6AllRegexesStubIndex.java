package edument.perl6idea.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.psi.Perl6RegexDecl;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class Perl6AllRegexesStubIndex extends StringStubIndexExtension<Perl6RegexDecl> {
    private static final int INDEX_VERSION = 3;
    private static final Perl6AllRegexesStubIndex instance = new Perl6AllRegexesStubIndex();

    public static Perl6AllRegexesStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, Perl6RegexDecl> getKey() {
        return Perl6StubIndexKeys.ALL_REGEXES;
    }
}
