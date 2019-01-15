class A {
    class B {
        method uses-self {
            self.foo(self);
        }
    }
    method foo($self) {
        say $self;
    }
}