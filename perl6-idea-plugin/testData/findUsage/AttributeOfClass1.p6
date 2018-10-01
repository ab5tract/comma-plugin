class Bar {
    has $!b<caret>ar;

    method test() {
        say $!bar;
    }

    method test2() {
        say $!bar;
    }
}
