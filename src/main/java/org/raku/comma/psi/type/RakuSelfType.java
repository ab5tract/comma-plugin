package org.raku.comma.psi.type;

import org.raku.comma.psi.RakuPackageDecl;

public class RakuSelfType extends RakuResolvedType {
    public RakuSelfType(RakuPackageDecl packageDecl) {
        super(packageDecl.getPackageName(), packageDecl);
    }
}
