package org.raku.psi.stub;

import org.raku.psi.RakuVariableDecl;

public interface RakuVariableDeclStub extends RakuDeclStub<RakuVariableDecl> {
    String[] getVariableNames();
    String getVariableType();
}
