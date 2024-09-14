class Outer {
    class Inner {
        has $!foo;

        method extract-from() {
            self.outer($!foo);
        }
    }
    method outer(Any $foo) {
        say $foo;
    }
}