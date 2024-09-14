class A {
    sub outer-sub() {
        say 1;
        say 'foo';
        say 3;
    }
    method a {
        sub a {
            say "Before outer";
            outer-sub();
            say "After new outer";
        }
    }
}