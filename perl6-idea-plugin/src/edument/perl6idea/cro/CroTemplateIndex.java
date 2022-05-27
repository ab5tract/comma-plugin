package edument.perl6idea.cro;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.psi.Perl6SubCall;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class CroTemplateIndex extends StringStubIndexExtension<Perl6SubCall> {
    private static final int INDEX_VERSION = 1;
    private static final CroTemplateIndex instance = new CroTemplateIndex();

    public static CroTemplateIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, Perl6SubCall> getKey() {
        return CroIndexKeys.CRO_TEMPLATE;
    }
}
