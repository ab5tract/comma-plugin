package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;
import edument.perl6idea.psi.stub.Perl6ScopedDeclStub;

public class Perl6RoutineDeclStubImpl extends StubBase<Perl6RoutineDecl> implements Perl6RoutineDeclStub {
    private final String routineName;
    private final String routineKind;
    private final boolean isExported;
    private final boolean isPrivate;
    private final String multiness;
    private final String returnType;

    public Perl6RoutineDeclStubImpl(StubElement stub, String name, String kind, boolean isPrivate,
                                    boolean exported, String multiness, String returnType) {
        super(stub, Perl6ElementTypes.ROUTINE_DECLARATION);
        this.routineName = name;
        this.routineKind = kind;
        this.isPrivate = isPrivate;
        this.isExported = exported;
        this.multiness = multiness;
        this.returnType = returnType;
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
    public boolean isPrivate() {
        return isPrivate;
    }

    @Override
    public boolean isExported() {
        return isExported;
    }

    @Override
    public String getMultiness() {
        return multiness;
    }

    @Override
    public String getReturnType() {
        return returnType;
    }
}
