sub foo($f<caret>oo) {
    say $foo;
    {
        $foo++;
    }
}
