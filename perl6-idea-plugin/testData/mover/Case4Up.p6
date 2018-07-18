sub foo() {
    sub bar() {
    <caret>}
    my @foo = one => 1,
                two => 2;
    say 3;
}
