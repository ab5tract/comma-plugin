package org.raku.comma.psi.type;

import org.raku.comma.psi.RakuPsiElement;

public class RakuResolvedType implements RakuType {
    private final String typename;
    private final RakuPsiElement resolution;

    public RakuResolvedType(String typename, RakuPsiElement resolution) {
        this.typename = typename;
        this.resolution = resolution;
    }

    @Override
    public String getName() {
        return typename;
    }

    public RakuPsiElement getResolution() {
        return resolution;
    }

    public boolean equals(Object other) {
        if (other instanceof RakuResolvedType)
            return ((RakuResolvedType)other).resolution == resolution;
        if (other instanceof RakuType)
            return ((RakuType)other).getName().equals(getName());
        return false;
    }
}
