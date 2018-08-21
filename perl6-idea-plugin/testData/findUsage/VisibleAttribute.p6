class A {
    has $.foo;

    method test {
        say $.foo;
        say $!foo;
    }
}
