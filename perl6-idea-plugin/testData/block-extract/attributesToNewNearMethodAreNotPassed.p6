class Foo {
    has $!a;
    has $.b;

    method one {
        self!two();
    }
    method !two() {
        say $a;
        say $b;
        say $.b;
        say $unknown-attribute;
    }
}