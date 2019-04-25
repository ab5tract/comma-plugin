my ($one, @three) = 5, +(1, 2, 3);

say $one;
say foo;
for @three -> $item {
    say $item;
}