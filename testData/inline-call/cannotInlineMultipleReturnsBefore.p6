sub many-rets($a, $b) {
    say 10;
    if $a > $b {
        return $a;
    }
    say 15;
    return $b;
}

sub foo() {
    say 14;
    ma<caret>ny-rets(5, 10);
    say 15;
}