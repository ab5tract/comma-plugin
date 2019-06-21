package edument.perl6idea.cro;

import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.psi.Perl6SubCall;

public class CroIndexKeys {
    public static final StubIndexKey<String, Perl6SubCall> CRO_ROUTES
            = StubIndexKey.createIndexKey("perl6.cro.routes");
}
