package org.raku.comma.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.comma.parsing.RakuElementTypes;
import org.raku.comma.psi.RakuRoutineDecl;
import org.raku.comma.psi.stub.RakuRoutineDeclStub;
import org.raku.comma.psi.stub.RakuScopedDeclStub;

public class RakuRoutineDeclStubImpl extends StubBase<RakuRoutineDecl> implements RakuRoutineDeclStub {
    private final String routineName;
    private final String routineKind;
    private final boolean isExported;
    private final boolean isPrivate;
    private final String multiness;
    private final String returnType;

    public RakuRoutineDeclStubImpl(StubElement stub, String name, String kind, boolean isPrivate,
                                   boolean exported, String multiness, String returnType) {
        super(stub, RakuElementTypes.ROUTINE_DECLARATION);
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
        return getParentStub() instanceof RakuScopedDeclStub
               ? ((RakuScopedDeclStub)getParentStub()).getScope()
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
