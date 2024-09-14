package org.raku.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.raku.psi.RakuFile;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class ProjectModulesStubIndex extends StringStubIndexExtension<RakuFile> {
    private static final int INDEX_VERSION = 3;
    private static final ProjectModulesStubIndex instance = new ProjectModulesStubIndex();

    public static ProjectModulesStubIndex getInstance() {
        return instance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, RakuFile> getKey() {
        return RakuStubIndexKeys.PROJECT_MODULES;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }
}
