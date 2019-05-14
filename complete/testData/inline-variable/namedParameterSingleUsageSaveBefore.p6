sub foo($a = 5, :$b = 10) {
    say $a;
    say $<caret>b + $a;
    $a;
}