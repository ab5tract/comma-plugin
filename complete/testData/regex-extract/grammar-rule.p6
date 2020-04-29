#!/usr/bin/env perl6

given /\w+ <[abcd]> \n? / {
    when / \w+ $foo { $bar } \n? / {}
}

grammar Foo {
    my $foo = 42;

    rule foo {
        <[abcd]> \n?
    }
    token foo {
        \w+ <foo>
    }
    token bar($a) {
        \w+ $a { $a + $foo } \n?
    }
}