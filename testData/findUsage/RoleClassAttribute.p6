role A {
    has $!foo;

    method foo {
        say $!f<caret>oo;
    }
}

class B does A {
    method foo {
        say $!foo;
    }
}

class C is B {
    method foo {
        say $!foo;
    }
}