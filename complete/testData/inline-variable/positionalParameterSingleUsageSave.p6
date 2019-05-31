sub foo($a = 5, :$b = 10) {
    say $a;
    say $b + 5;
    $a;
}