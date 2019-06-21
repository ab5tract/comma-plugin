package edument.perl6idea.cro;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.stub.index.Perl6AllAttributesStubIndex;
import org.jetbrains.annotations.NotNull;

public class CroRouteIndex extends StringStubIndexExtension<Perl6SubCall> {
    private static final int INDEX_VERSION = 1;
    private static final CroRouteIndex instance = new CroRouteIndex();

    public static CroRouteIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, Perl6SubCall> getKey() {
        return CroIndexKeys.CRO_ROUTES;
    }
}
