package org.raku.comma.psi.type;

public record RakuCoercionType(RakuType targetType, RakuType sourceType) implements RakuType {

    @Override
    public String getName() {
        return targetType.getName() + "(" + sourceType.getName() + ")";
    }

    @Override
    public RakuType nominalType() {
        return targetType.nominalType();
    }

    public boolean equals(Object other) {
        return other instanceof RakuCoercionType &&
               targetType.equals(((RakuCoercionType)other).targetType) &&
               sourceType.equals(((RakuCoercionType)other).sourceType);
    }
}
