sub foo($one, $two) {
    foo(1, 2);
    say foo(1, 2);
}

my $new = &foo;
foo(1, 2) + foo(1, 2);