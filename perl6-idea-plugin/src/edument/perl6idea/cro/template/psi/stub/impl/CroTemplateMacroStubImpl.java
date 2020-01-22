package edument.perl6idea.cro.template.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.cro.template.parsing.CroTemplateElementTypes;
import edument.perl6idea.cro.template.psi.CroTemplateMacro;
import edument.perl6idea.cro.template.psi.stub.CroTemplateMacroStub;

public class CroTemplateMacroStubImpl extends StubBase<CroTemplateMacro> implements CroTemplateMacroStub {
    private final String name;

    public CroTemplateMacroStubImpl(StubElement stub, String name) {
        super(stub, CroTemplateElementTypes.MACRO);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
