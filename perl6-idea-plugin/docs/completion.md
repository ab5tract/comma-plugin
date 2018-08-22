### Principles of completion in Comma

This document describes main principles of completion algorithm used in Comma
and provides set of rules used. It must be noted that algorithm presented
has no 100% matching with real Perl 6 compiler, but strives to be as close as
possible.

#### Inheritance (`is`)

* Possible inheritance schemes are:
  * `Class is Class`
  * `Class is Role`
  * `Role is Class`
  * `Role is Role`
* Private methods are **never** contributed
* Private attributes are **never** contributed
* Methods are always contributed (regardless of `role` or `class` being superclass)
* Attributes are always contributed

#### Composition (`does`)

* Possible composition schemes are:
  * `Class does Role`
    * Private methods are contributed
    * Private attributes are contributed
    * Methods are always contributed (regardless of `role` or `class` being superclass)
    * Attributes are always contributed
  * `Role does Role`
    * Private methods are contributed
    * Private attributes are **never** contributed
    * Methods are always contributed (regardless of `role` or `class` being superclass)
    * Attributes are always contributed

#### Implementation details

Considering restrictions given above, SymbolCollector has number of flags:

##### Package depth

This flag indicates where exactly collector gathers symbols, starting from callee as 0
and increasing its value with every package boundary crossed (i.e. every `getParent` of current scope).

##### Relevance of instance symbols

Given the case of

    class Foo {
        has $.a;

        class Bar {
            method test { say $.<caret> }
        }
    }

we must not complete `$.a` attribute. For this purpose, we check this flag and contribute if
symbols are allowed.

##### Collect of internal parts

Main flag that is used to indicate whether we export private methods and attributes or not.

##### Nesting level

Nesting level indicates how much did we ascend to parents of class/role. It initially starts from zero
and is increased when inheritance/composition chain is used to contribute symbols.

For example

    role A {}
    role B does A {}
    class C does B {}

Nesting level of C is 1, B - 2, A - 3.

It must be noted that this level is preserved regardless of real recursive nature of
`contributeFromElders` method.

For example

    role A {}
    role B {}
    class C does A is B {}

Level of C is 1, level of B *and* A is 1 regardless of their order in declaration.

##### Broken compositional chain flag

When walking over composition/inheritance chain, it is important to consider that not
every type of chain preserves all passed elements.

For example

    role A {
        has $!foo;
    }
    class B does A {}
    class Test is B {}

Because of inheritance rules, attribute `$!foo` is accessible in `B`, *but*
is not accessible from `Test`. When it happens that `class` "steals" internals provided
by role, it means that the "chain" is broken: regardless of what roles are there,
their internals are not accessible because of one (or more) level of inheritance indirection.
