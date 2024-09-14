package org.raku.psi.type;

/* Done by all kinds of type that we may infer. */
public interface RakuType {
    /* Get a displayable name for the type. */
    String getName();

    /* Get the nominal type for this type. */
    default RakuType nominalType() {
        return this;
    }

    /* Get the type to use for method dispatch for this type. */
    default RakuType dispatchType() {
        RakuType nominal = nominalType();
        return nominal == this ? nominal : nominal.dispatchType();
    }

    default boolean isEqual(RakuType other) {
        return this == other;
    }
}
