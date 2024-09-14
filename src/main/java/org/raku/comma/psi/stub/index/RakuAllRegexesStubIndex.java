package org.raku.comma.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.raku.comma.psi.RakuRegexDecl;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class RakuAllRegexesStubIndex extends StringStubIndexExtension<RakuRegexDecl> {
    private static final int INDEX_VERSION = 3;
    private static final RakuAllRegexesStubIndex instance = new RakuAllRegexesStubIndex();

    public static RakuAllRegexesStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RakuRegexDecl> getKey() {
        return RakuStubIndexKeys.ALL_REGEXES;
    }
}
