package org.raku.psi.stub;

import org.raku.psi.RakuRoutineDecl;

public interface RakuRoutineDeclStub extends RakuDeclStub<RakuRoutineDecl> {
    String getRoutineKind();
    String getRoutineName();
    boolean isPrivate();
    String getMultiness();
    String getReturnType();
}
