role Oldest {
    has $!foo-like-thing;
}

role A does Oldest {
    has $!foo;
    method foo { say $!foo; }
}

class B does A {
    method foo { say $!foo; }
}

class C is B {
    method foo { say $!f<caret>; }
}
