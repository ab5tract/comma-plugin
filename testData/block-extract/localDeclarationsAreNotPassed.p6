sub extracted(Any $aa) {
    my $inner;
    say $inner.defined;
    say 'inner';
    say $aa;
}
sub foo($aa) {
    extracted($aa);
    say 'outer';
}