class A {
    method a {
        my $res = do <caret>if True {
            42
        }
        else {
            24
        }
        with 1 {}
    }
}