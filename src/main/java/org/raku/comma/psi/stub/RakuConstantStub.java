package org.raku.comma.psi.stub;

import org.raku.comma.psi.RakuConstant;

public interface RakuConstantStub extends RakuDeclStub<RakuConstant> {
    String getConstantName();
}
