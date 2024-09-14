package org.raku.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.parsing.RakuElementTypes;
import org.raku.psi.RakuPackageDecl;
import org.raku.psi.stub.RakuPackageDeclStub;
import org.raku.psi.stub.RakuScopedDeclStub;

public class RakuPackageDeclStubImpl extends StubBase<RakuPackageDecl> implements RakuPackageDeclStub {
    private final String packageKind;
    private final String packageName;
    private final boolean isExported;

    public RakuPackageDeclStubImpl(StubElement parent, String packageKind, String packageName, boolean exported) {
        super(parent, RakuElementTypes.PACKAGE_DECLARATION);
        this.packageKind = packageKind;
        this.packageName = packageName;
        isExported = exported;
    }

    @Override
    public String getPackageKind() {
        return packageKind;
    }

    @Override
    public String getTypeName() {
        return packageName;
    }

    @Override
    public String getScope() {
        return getParentStub() instanceof RakuScopedDeclStub
               ? ((RakuScopedDeclStub)getParentStub()).getScope()
               : "our";
    }

    @Override
    public boolean isExported() {
        return isExported;
    }
}
