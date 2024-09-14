package org.raku.comma.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.raku.comma.psi.RakuVariableDecl;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class RakuDynamicVariablesStubIndex extends StringStubIndexExtension<RakuVariableDecl> {
    private static final int INDEX_VERSION = 1;
    private static final RakuDynamicVariablesStubIndex instance = new RakuDynamicVariablesStubIndex();

    public static RakuDynamicVariablesStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @Override
    public @NotNull StubIndexKey<String, RakuVariableDecl> getKey() {
        return RakuStubIndexKeys.DYNAMIC_VARIABLES;
    }
}
