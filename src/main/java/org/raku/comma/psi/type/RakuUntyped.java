package org.raku.comma.psi.type;

public class RakuUntyped implements RakuType {
    public static final RakuUntyped INSTANCE = new RakuUntyped();

    private RakuUntyped() {}

    @Override
    public String getName() {
        return "Any";
    }

    public boolean equals(Object other) {
        return other instanceof RakuUntyped;
    }
}
