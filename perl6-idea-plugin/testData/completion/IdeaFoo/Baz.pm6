role Baz1 {
    has $!private;
    has $.visible;

    method !private {}
    method !private2 {}
    method visible {}
}

role IdeaFoo::Bar::Baz2 {
    has $!private;
    has $.visible;

    method !private {}
    method visible {}
}

package Outer {
    role Baz3 {
        has $!private;
        has $.visible;

        method !private {}
        method visible {}
    }
}

class Foo::Bar::Baz {
    method !private {}
    method visible {}
}

sub foo-one {}
sub foo-two is export {}
