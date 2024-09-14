sub foo() {}

foo;

{
    foo;
}

my $a = foo;

sub a($default = &f<caret>oo) {
    $default();
}