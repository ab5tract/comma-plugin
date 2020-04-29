#!/usr/bin/env perl6

given /\w+ <selection><[abcd]> \n?</selection> / {
    when / \w+ $foo { $bar } \n? / {}
}

grammar Foo {
    my $foo = 42;

    token foo { \w+ <[abcd]> \n? }
    token bar($a) { \w+ $a { $a + $foo } \n? }
}