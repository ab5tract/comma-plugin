package org.raku.comma.cro.template.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.raku.comma.cro.template.psi.CroTemplateSub;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class CroTemplateSubIndex extends StringStubIndexExtension<CroTemplateSub> {
    private static final int INDEX_VERSION = 1;
    private static final CroTemplateSubIndex instance = new CroTemplateSubIndex();

    public static CroTemplateSubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, CroTemplateSub> getKey() {
        return CroTemplateStubIndexKeys.TEMPLATE_SUB;
    }
}
