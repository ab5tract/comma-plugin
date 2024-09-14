class Bar {
    has $!bar;

    method test() {
        say $!<caret>bar;
    }

    method test2() {
        say $!bar;
    }
}
