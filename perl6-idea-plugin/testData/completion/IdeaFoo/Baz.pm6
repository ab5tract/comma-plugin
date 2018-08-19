role Baz1 {
    has $!private;
    has $.visible;
}

role IdeaFoo::Bar::Baz2 {
    has $!private;
    has $.visible;
}

package Outer {
    role Baz3 {
        has $!private;
        has $.visible;
    }
}