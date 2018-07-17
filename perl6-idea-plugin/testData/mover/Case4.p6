sub foo() {
    sub bar() {
        my @foo = one => 1,
                two => 2;
    <caret>}
    say 3;
}
