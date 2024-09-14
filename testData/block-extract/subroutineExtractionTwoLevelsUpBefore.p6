class A {
    method a {
        sub a {
            say "Before outer";
            <selection>say 1;
            say 'foo';
            say 3;</selection>
            say "After new outer";
        }
    }
}