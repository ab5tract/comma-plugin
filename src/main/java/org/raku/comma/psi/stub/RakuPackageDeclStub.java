package org.raku.comma.psi.stub;

import org.raku.comma.psi.RakuPackageDecl;

public interface RakuPackageDeclStub extends RakuTypeStub<RakuPackageDecl> {
    String getPackageKind();
}
