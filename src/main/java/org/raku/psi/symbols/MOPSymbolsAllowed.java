package org.raku.psi.symbols;

public class MOPSymbolsAllowed {

    /**
    * If private methods are visible. This is the case if we are
     * inside of the declaration of the object, or inside a role
     * immediately done (transitively) it. As soon as we walk the
     * inheritance chain, and step away from that, it becomes false.
     * If we are doing a resolution on a symbol other than one in
     * the current class (for example, another object we inferred
     * the type of), then it starts out false.
    */
    public final boolean privateMethodsVisible;

    /**
     * Are private attributes visible.
      */
    public final boolean privateAttributesVisible;

    /**
     * If submethods are visible. This is the case at the start of a
     * lookup, and becomes false once we reach an inheritance boundary.
     */
    public final boolean submethodsVisible;

    /**
     * Whether we started the lookup in a role; in this case, the
     * attributes of another "does" are not visible.
     */
    public final boolean startedInRole;

    public MOPSymbolsAllowed(boolean privateMethodsVisible, boolean privateAttributesVisible,
                             boolean submethodsVisible, boolean startedInRole) {
        this.privateMethodsVisible = privateMethodsVisible;
        this.privateAttributesVisible = privateAttributesVisible;
        this.submethodsVisible = submethodsVisible;
        this.startedInRole = startedInRole;
    }

    public MOPSymbolsAllowed does() {
        return startedInRole && privateAttributesVisible
                ? new MOPSymbolsAllowed(privateMethodsVisible, false, submethodsVisible, startedInRole)
                : this;
    }

    public MOPSymbolsAllowed is() {
        return new MOPSymbolsAllowed(false, false, false, startedInRole);
    }
}
