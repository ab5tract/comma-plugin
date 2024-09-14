class Bar {
    method a {
        sub foo($a?) {
            my $var;
            my $bar;
            self!mm<caret>m(1, 1, [1], (2), named => 1, double => 2, double => 2, foo, foo(1), Bar.value(), $var, $bar);
        }
    }
}