my ($two, @three) = foo, +(1, 2, 3);

say 5;
say $two;
for @three -> $item {
    say $item;
}