// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.psi.symbols;

/**
 * Contributes symbols that are stored against the meta-object of a type, such as
 * methods, private methods, and attributes.
 */
public interface Perl6MOPSymbolContributor {
    /**
     * Called with a collector to contribute symbols that are on the meta-object of the
     * element implementing this interface.
     * @param collector The collector to tell symbols to.
     * @param privatesVisible If private elements are visible. This is the case if we are
     *                        inside of the declaration of the object, or inside a role
     *                        immediately done by it. As soon as we walk the inheritance
     *                        chain, and step away from that, it becomes false. If we are
     *                        doing a resolution on a symbol other than one in the current
     *                        class (for example, another object we inferred the type of),
     *                        then it starts out false.
     * @param submethodsVisible If submethods are visible. This is the case at the start
     *                          of a lookup, and becomes false once we reach an inheritance
     *                          boundary.
     */
    void contributeMOPSymbols(Perl6SymbolCollector collector, boolean privatesVisible,
                              boolean submethodsVisible);
}
