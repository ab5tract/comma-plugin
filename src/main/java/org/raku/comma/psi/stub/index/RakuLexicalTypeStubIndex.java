package org.raku.comma.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class RakuLexicalTypeStubIndex extends StringStubIndexExtension<RakuIndexableType> {
    private static final int INDEX_VERSION = 3;
    private static final RakuLexicalTypeStubIndex instance = new RakuLexicalTypeStubIndex();

    public static RakuLexicalTypeStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RakuIndexableType> getKey() {
        return RakuStubIndexKeys.LEXICAL_TYPES;
    }
}
