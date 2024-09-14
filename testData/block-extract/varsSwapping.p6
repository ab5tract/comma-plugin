sub sum(Any $two, Any $one, Any $three) {
    say $one + $two + $three;
}
{
    my $one;
    my $two;
    my $three;
    sum($two, $one, $three);
}