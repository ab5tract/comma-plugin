package org.raku.comma.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.raku.comma.psi.RakuVariableDecl;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class RakuAllAttributesStubIndex extends StringStubIndexExtension<RakuVariableDecl> {
    private static final int INDEX_VERSION = 4;
    private static final RakuAllAttributesStubIndex instance = new RakuAllAttributesStubIndex();

    public static RakuAllAttributesStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RakuVariableDecl> getKey() {
        return RakuStubIndexKeys.ALL_ATTRIBUTES;
    }
}
