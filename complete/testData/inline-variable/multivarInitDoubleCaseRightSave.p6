my ($one, $two, @three) = 5, foo, +(1, 2, 3);

say $one;
say $two;
for +(1, 2, 3) -> $item {
    say $item;
}