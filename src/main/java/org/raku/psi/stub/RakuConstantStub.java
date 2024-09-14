package org.raku.psi.stub;

import org.raku.psi.RakuConstant;

public interface RakuConstantStub extends RakuDeclStub<RakuConstant> {
    String getConstantName();
}
