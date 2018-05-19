package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;

public class Perl6RoutineDeclStubImpl extends StubBase<Perl6RoutineDecl> implements Perl6RoutineDeclStub {
    private String routineName;
    private String routineKind;
    private boolean isExported;

    public Perl6RoutineDeclStubImpl(StubElement stub, String name, String kind, boolean exported) {
        super(stub, Perl6ElementTypes.ROUTINE_DECLARATION);
        this.routineName = name;
        this.routineKind = kind;
        isExported = exported;
    }

    @Override
    public String getRoutineName() {
        return routineName;
    }

    @Override
    public String getRoutineKind() { return routineKind; }

    @Override
    public String getScope() {
        return getParentStub() instanceof Perl6ScopedDeclStub
               ? ((Perl6ScopedDeclStub)getParentStub()).getScope()
               : (routineKind.equals("sub") || routineKind.isEmpty() ? "my" : "has");
    }

    @Override
    public boolean isExported() {
        return isExported;
    }
}
