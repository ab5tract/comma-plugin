package edument.perl6idea.cro.template.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.cro.template.parsing.CroTemplateElementTypes;
import edument.perl6idea.cro.template.psi.CroTemplatePart;
import edument.perl6idea.cro.template.psi.stub.CroTemplatePartStub;

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
