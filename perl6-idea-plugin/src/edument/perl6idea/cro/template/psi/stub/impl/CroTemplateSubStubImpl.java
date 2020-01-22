package edument.perl6idea.cro.template.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.cro.template.parsing.CroTemplateElementTypes;
import edument.perl6idea.cro.template.psi.CroTemplateSub;
import edument.perl6idea.cro.template.psi.stub.CroTemplateSubStub;

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
