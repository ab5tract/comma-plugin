#!/usr/bin/env raku

given /\w+ <[abcd]> \n? / {
    when / \w+ $foo { $bar } \n? / {}
}

grammar Foo {
    my $foo = 42;

    token foo {
        \w+ <[abcd]> \n?
    }
    token foo(Any $a, Int $foo) {
        $a { $a + $foo } \n?
    }
    token bar($a) {
        \w+ <.foo>
    }
}