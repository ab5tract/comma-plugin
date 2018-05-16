package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;

public class Perl6RoutineDeclStubImpl extends StubBase<Perl6RoutineDecl> implements Perl6RoutineDeclStub {
    private String routineName;

    public Perl6RoutineDeclStubImpl(StubElement stub, String name) {
        super(stub, Perl6ElementTypes.ROUTINE_DECLARATION);
        this.routineName = name;
    }

    @Override
    public String getRoutineName() {
        return routineName;
    }
}
