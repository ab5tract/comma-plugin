#!/usr/bin/env perl6

my token foo {
    <[abcd]> \n?
}
given /\w+ <&foo> / {
    when / \w+ $foo { $bar } \n? / {}
}

grammar Foo {
    my $foo = 42;

    token foo {
        \w+ <[abcd]> \n?
    }
    token bar($a) {
        \w+ $a { $a + $foo } \n?
    }
}