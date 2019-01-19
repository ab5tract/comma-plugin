sub foo(Any $bar) {
    my $foo;
    say $bar;
    say $bar;
    say $foo;
}
{
    my $bar;
    foo($bar);
}