sub foo($a = 5, :$b = 10) {
    say $a;
    say 10 + $a;
    $a;
}