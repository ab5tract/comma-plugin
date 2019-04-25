my ($one, $two, @three) = 5, foo, +(1, 2, 3);

say $one;
say $two;
for @th<caret>ree -> $item {
    say $item;
}