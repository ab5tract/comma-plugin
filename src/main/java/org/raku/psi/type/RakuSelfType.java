package org.raku.psi.type;

import org.raku.psi.RakuPackageDecl;

public class RakuSelfType extends RakuResolvedType {
    public RakuSelfType(RakuPackageDecl packageDecl) {
        super(packageDecl.getPackageName(), packageDecl);
    }
}
