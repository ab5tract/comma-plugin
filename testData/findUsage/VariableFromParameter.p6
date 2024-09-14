sub foo($foo) {
    say $foo;
    {
        $fo<caret>o++;
    }
}
