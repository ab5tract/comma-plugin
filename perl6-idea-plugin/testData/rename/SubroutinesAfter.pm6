sub renamed-sub($one, $two) {
    renamed-sub(1, 2);
    say renamed-sub(1, 2);
}

my $new = &renamed-sub;
renamed-sub(1, 2) + renamed-sub(1, 2);