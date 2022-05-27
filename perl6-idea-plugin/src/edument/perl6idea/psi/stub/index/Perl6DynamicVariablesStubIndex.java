package edument.perl6idea.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.psi.Perl6VariableDecl;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class Perl6DynamicVariablesStubIndex extends StringStubIndexExtension<Perl6VariableDecl> {
    private static final int INDEX_VERSION = 1;
    private static final Perl6DynamicVariablesStubIndex instance = new Perl6DynamicVariablesStubIndex();

    public static Perl6DynamicVariablesStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, Perl6VariableDecl> getKey() {
        return Perl6StubIndexKeys.DYNAMIC_VARIABLES;
    }
}
