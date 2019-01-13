sub do-magic(Int $a, Any @b, Int $number) {
    say $a;
    say @b>>.Str.join('foo');
    say "Magic number is $number";
    say 15 + 15;
}
{
    my $a = 5;
    my @b = 1, 2, 3;
    my $number = 42;

    do-magic($a, @b, $number);
    say "I will be outside!";
}