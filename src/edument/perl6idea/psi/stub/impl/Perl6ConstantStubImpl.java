package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6Constant;
import edument.perl6idea.psi.stub.Perl6ConstantStub;

public class Perl6ConstantStubImpl extends StubBase<Perl6Constant> implements Perl6ConstantStub {
    private final String constantName;

    public Perl6ConstantStubImpl(StubElement stub, String name) {
        super(stub, Perl6ElementTypes.CONSTANT);
        this.constantName = name;
    }

    @Override
    public String getConstantName() {
        return constantName;
    }
}
