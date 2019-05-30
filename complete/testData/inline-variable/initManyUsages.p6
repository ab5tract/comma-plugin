say 24 + 42;
say 24 + 42 + 24 + 42;

foo(24 + 42, 24 + 42);

role A[:$foo = 24 + 42] {
    method a {
        say $foo;
    }
}