package org.raku.comma.psi.stub;

import org.raku.comma.psi.RakuRoutineDecl;

public interface RakuRoutineDeclStub extends RakuDeclStub<RakuRoutineDecl> {
    String getRoutineKind();
    String getRoutineName();
    boolean isPrivate();
    String getMultiness();
    String getReturnType();
}
