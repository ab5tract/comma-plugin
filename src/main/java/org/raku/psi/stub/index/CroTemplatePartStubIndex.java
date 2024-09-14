package org.raku.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.raku.cro.template.psi.CroTemplatePart;
import org.raku.cro.template.psi.stub.index.CroTemplateStubIndexKeys;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class CroTemplatePartStubIndex extends StringStubIndexExtension<CroTemplatePart> {
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
    public StubIndexKey<String, CroTemplatePart> getKey() {
        return CroTemplateStubIndexKeys.TEMPLATE_PART;
    }
}
