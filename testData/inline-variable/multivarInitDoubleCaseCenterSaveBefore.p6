my ($one, $two, @three) = 5, foo, +(1, 2, 3);

say $one;
say $t<caret>wo;
for @three -> $item {
    say $item;
}