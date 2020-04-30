#!/usr/bin/env perl6

given /\w+ <[abcd]> \n? / {
    when / \w+ $foo { $bar } \n? / {}
}

grammar Foo {
    my $foo = 42;

    token foo { \w+ <selection><[abcd]> \n?</selection> }
    token bar($a) { \w+ $a { $a + $foo } \n? }
}