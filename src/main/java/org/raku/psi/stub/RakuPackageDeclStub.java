package org.raku.psi.stub;

import org.raku.psi.RakuPackageDecl;

public interface RakuPackageDeclStub extends RakuTypeStub<RakuPackageDecl> {
    String getPackageKind();
}
