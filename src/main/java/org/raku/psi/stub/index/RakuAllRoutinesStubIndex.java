package org.raku.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.raku.psi.RakuRoutineDecl;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class RakuAllRoutinesStubIndex extends StringStubIndexExtension<RakuRoutineDecl> {
    private static final int INDEX_VERSION = 6;
    private static final RakuAllRoutinesStubIndex instance = new RakuAllRoutinesStubIndex();

    public static RakuAllRoutinesStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RakuRoutineDecl> getKey() {
        return RakuStubIndexKeys.ALL_ROUTINES;
    }
}
