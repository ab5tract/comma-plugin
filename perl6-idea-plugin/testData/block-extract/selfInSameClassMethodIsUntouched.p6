class Foo {
    method outer() {
        say 42;
        self.inner();
        say 42;
    }
    method inner() {
        for 1 .. ^10 {
            say self;
        }
    }
}