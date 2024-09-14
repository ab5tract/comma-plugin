class A {
    method a($var) {
        self!foo($var);
    }
    method !foo(Any $var) {
        my $new = $var.key;
        say $new;
    }
}