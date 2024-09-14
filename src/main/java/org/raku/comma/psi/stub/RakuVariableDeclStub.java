package org.raku.comma.psi.stub;

import org.raku.comma.psi.RakuVariableDecl;

public interface RakuVariableDeclStub extends RakuDeclStub<RakuVariableDecl> {
    String[] getVariableNames();
    String getVariableType();
}
