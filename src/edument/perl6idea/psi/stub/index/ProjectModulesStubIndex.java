package edument.perl6idea.psi.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.psi.Perl6File;
import org.jetbrains.annotations.NotNull;

public class ProjectModulesStubIndex extends StringStubIndexExtension<Perl6File> {
    @NotNull
    @Override
    public StubIndexKey<String, Perl6File> getKey() {
        return Perl6StubIndexKeys.PROJECT_MODULES;
    }
}
