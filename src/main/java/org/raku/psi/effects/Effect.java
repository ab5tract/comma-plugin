package org.raku.psi.effects;

public class Effect {
    public static final Effect IMPURE = new Effect();
    public static final Effect DECLARATION = new Effect();

    private Effect() {}
}
