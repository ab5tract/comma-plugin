my $<caret>a = 24 + 42;

say $a;
say $a + $a;

foo($a, $a);

role A[:$foo = $a] {
    method a {
        say $foo;
    }
}