my ($one, $two, @th<caret>ree) = 5, foo, +(1, 2, 3);

say $one;
say $two;
for @three -> $item {
    say $item;
}