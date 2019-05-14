my ($one, $two, @three) = 5, foo, +(1, 2, 3);

say $one;
say foo;
for @three -> $item {
    say $item;
}