class AA {
    has $!foo;

    method a {
        sub inner-lexical() {
            say $!foo + $!foo;
        }
        inner-lexical();
    }
}