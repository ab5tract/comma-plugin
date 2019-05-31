my ($one, $two, @three) = 5, foo, +(1, 2, 3);

say 5;
say $two;
for @three -> $item {
    say $item;
}