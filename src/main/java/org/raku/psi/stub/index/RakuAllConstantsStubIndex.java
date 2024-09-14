package org.raku.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.raku.psi.RakuConstant;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class RakuAllConstantsStubIndex extends StringStubIndexExtension<RakuConstant> {
    private static final int INDEX_VERSION = 3;
    private static final RakuAllConstantsStubIndex instance = new RakuAllConstantsStubIndex();

    public static RakuAllConstantsStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RakuConstant> getKey() {
        return RakuStubIndexKeys.ALL_CONSTANTS;
    }
}
