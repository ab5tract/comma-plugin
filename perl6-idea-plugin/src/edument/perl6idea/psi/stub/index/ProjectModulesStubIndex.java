package edument.perl6idea.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.psi.Perl6File;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class ProjectModulesStubIndex extends StringStubIndexExtension<Perl6File> {
    private static final int INDEX_VERSION = 3;
    private static final ProjectModulesStubIndex instance = new ProjectModulesStubIndex();

    public static ProjectModulesStubIndex getInstance() {
        return instance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, Perl6File> getKey() {
        return Perl6StubIndexKeys.PROJECT_MODULES;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }
}
