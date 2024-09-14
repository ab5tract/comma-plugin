role Foo {
    has $!foo;
}

class A does Foo {
    method test {
        $!foo++;
        say $!f<caret>oo;
    }
}
