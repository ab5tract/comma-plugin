sub foo($a = 5, :$<caret>b = 10) {
    say $a;
    say $b + $a;
    $a;
}