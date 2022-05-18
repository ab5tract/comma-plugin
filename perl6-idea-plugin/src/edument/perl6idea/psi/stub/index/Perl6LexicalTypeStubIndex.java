package edument.perl6idea.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class Perl6LexicalTypeStubIndex extends StringStubIndexExtension<Perl6IndexableType> {
    private static final int INDEX_VERSION = 3;
    private static final Perl6LexicalTypeStubIndex instance = new Perl6LexicalTypeStubIndex();

    public static Perl6LexicalTypeStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, Perl6IndexableType> getKey() {
        return Perl6StubIndexKeys.LEXICAL_TYPES;
    }
}
