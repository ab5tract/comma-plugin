sub foo($<caret>a = 5, :$b = 10) {
    say $a;
    say $b + $a;
    $a;
}