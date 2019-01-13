sub do-magic(Int $a, Str @values, Str $number) {
    say $a;
    say +@values;
    say "Magic number is $number";
    say 15 + 15;
}
sub outer(Str @values) {
    state Int $a = 5;
    my $number = '42';
    do-magic($a, @values, $number);
    say "I am in outer";
}