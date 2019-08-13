sub foo {
    say 15;
    20;
}

say foo() + do {say 15;
20;};