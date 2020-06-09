class A {
    method a {
        my $res = do <caret>self.foo();
        with 1 {}
    }
    method foo() {
        if True {
            42
        }
        else {
            24
        }
    }
}