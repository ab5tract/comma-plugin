package edument.perl6idea.psi.stub.index;

import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.psi.Perl6File;

public class Perl6StubIndexKeys {
    public static final StubIndexKey<String, Perl6File> PROJECT_MODULES
        = StubIndexKey.createIndexKey("perl6.projectModules");
}
