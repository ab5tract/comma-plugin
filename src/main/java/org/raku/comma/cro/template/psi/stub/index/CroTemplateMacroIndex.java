package org.raku.comma.cro.template.psi.stub.index;

import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.raku.comma.cro.template.psi.CroTemplateMacro;
import org.jetbrains.annotations.NotNull;

@InternalIgnoreDependencyViolation
public class CroTemplateMacroIndex extends StringStubIndexExtension<CroTemplateMacro> {
    private static final int INDEX_VERSION = 1;

    private static final CroTemplateMacroIndex instance = new CroTemplateMacroIndex();

    public static CroTemplateMacroIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, CroTemplateMacro> getKey() {
        return CroTemplateStubIndexKeys.TEMPLATE_MACRO;
    }
}
