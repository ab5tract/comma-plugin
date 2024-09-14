package org.raku.cro.template.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.cro.template.parsing.CroTemplateElementTypes;
import org.raku.cro.template.psi.CroTemplatePart;
import org.raku.cro.template.psi.stub.CroTemplatePartStub;

public class CroTemplatePartStubImpl extends StubBase<CroTemplatePart> implements CroTemplatePartStub {
    private final String name;

    public CroTemplatePartStubImpl(StubElement<?> stub, String name) {
        super(stub, CroTemplateElementTypes.PART);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
