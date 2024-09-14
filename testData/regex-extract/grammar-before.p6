#!/usr/bin/env raku

given /\w+ <[abcd]> \n? / {
    when / \w+ $foo { $bar } \n? / {}
}

grammar Foo {
    my $foo = 42;

    token foo { \w+ <[abcd]> \n? }
    token bar($a) { \w+ <selection>$a { $a + $foo } \n?</selection> }
}