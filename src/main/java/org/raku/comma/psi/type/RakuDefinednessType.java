package org.raku.comma.psi.type;

public record RakuDefinednessType(RakuType baseType, boolean requiresDefined) implements RakuType {

    @Override
    public String getName() {
        return baseType.getName() + (requiresDefined ? ":D" : ":U");
    }

    @Override
    public RakuType nominalType() {
        return baseType.nominalType();
    }

    public boolean equals(Object other) {
        return other instanceof RakuDefinednessType &&
               baseType.equals(((RakuDefinednessType)other).baseType) &&
               requiresDefined == ((RakuDefinednessType)other).requiresDefined;
    }
}
