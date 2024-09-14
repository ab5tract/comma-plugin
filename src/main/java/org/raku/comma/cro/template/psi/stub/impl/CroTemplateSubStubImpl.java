package org.raku.comma.cro.template.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.comma.cro.template.parsing.CroTemplateElementTypes;
import org.raku.comma.cro.template.psi.CroTemplateSub;
import org.raku.comma.cro.template.psi.stub.CroTemplateSubStub;

public class CroTemplateSubStubImpl extends StubBase<CroTemplateSub> implements CroTemplateSubStub {
    private final String name;

    public CroTemplateSubStubImpl(StubElement stub, String name) {
        super(stub, CroTemplateElementTypes.SUB);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
