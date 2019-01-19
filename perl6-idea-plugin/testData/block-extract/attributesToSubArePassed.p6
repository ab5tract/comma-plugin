class {
    has $.a;
    has Int @!b;

    sub foo($self, Any $a, @b) {
        say $a + $self.a;
        say @b + $self.b;
    }
    method a {
        foo(self, $!a, @!b);
    }
}