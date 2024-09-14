role Foo {
    has $!f<caret>oo;
}

class A does Foo {
    method test {
        $!foo++;
        say $!foo;
    }
}
