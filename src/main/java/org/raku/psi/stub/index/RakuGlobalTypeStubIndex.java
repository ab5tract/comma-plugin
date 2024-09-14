package org.raku.psi.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class RakuGlobalTypeStubIndex extends StringStubIndexExtension<RakuIndexableType> {
    private static final int INDEX_VERSION = 4;
    private static final RakuGlobalTypeStubIndex instance = new RakuGlobalTypeStubIndex();

    public static RakuGlobalTypeStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RakuIndexableType> getKey() {
        return RakuStubIndexKeys.GLOBAL_TYPES;
    }
}
