package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6Constant;
import edument.perl6idea.psi.stub.Perl6ConstantStub;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;
import org.jetbrains.annotations.Nullable;

public class Perl6ConstantStubImpl extends StubBase<Perl6Constant> implements Perl6ConstantStub {
    @Nullable
    private final String constantName;
    private final boolean isExported;

    public Perl6ConstantStubImpl(StubElement stub, @Nullable String name, boolean isExported) {
        super(stub, Perl6ElementTypes.CONSTANT);
        this.constantName = name;
        this.isExported = isExported;
    }

    @Nullable
    @Override
    public String getConstantName() {
        return constantName;
    }

    @Override
    public String getScope() {
        return getParentStub() instanceof Perl6ScopedDeclStub
               ? ((Perl6ScopedDeclStub)getParentStub()).getScope()
               : "our";
    }

    @Override
    public boolean isExported() {
        return isExported;
    }
}
