sub foo {
    say 14;
    15;
    return 14;
}

say 10 + do {
    say 14;
    15;
    14;
} + 20;