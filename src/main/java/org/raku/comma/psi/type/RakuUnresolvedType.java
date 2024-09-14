package org.raku.comma.psi.type;

public class RakuUnresolvedType implements RakuType {
    private final String typename;

    public RakuUnresolvedType(String typename) {
        this.typename = typename;
    }

    @Override
    public String getName() {
        return typename;
    }

    public boolean equals(Object other) {
        if (other instanceof RakuType)
            return ((RakuType)other).getName().equals(getName());
        return false;
    }
}
