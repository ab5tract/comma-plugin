sub outer(Str @values) {
    state Int $a = 5;
    my $number = '42';
    <selection>say $a;
    say +@values;
    say "Magic number is $number";
    say 15 </selection>+ 15;
    say "I am in outer";
}