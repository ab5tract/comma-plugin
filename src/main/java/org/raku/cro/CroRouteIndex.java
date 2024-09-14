package org.raku.cro;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.raku.psi.RakuSubCall;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class CroRouteIndex extends StringStubIndexExtension<RakuSubCall> {
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
    public StubIndexKey<String, RakuSubCall> getKey() {
        return CroIndexKeys.CRO_ROUTES;
    }
}
