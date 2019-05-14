my ($one, $two, @three) = 5, foo, +(1, 2, 3);

say $o<caret>ne;
say $two;
for @three -> $item {
    say $item;
}