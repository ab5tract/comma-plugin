sub foo($a = 5, :$b = 10) {
    say $a;
    say $b + $a;
    $<caret>a;
}