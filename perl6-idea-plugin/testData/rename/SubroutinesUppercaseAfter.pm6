sub Renamed-sub($one, $two) {
    Renamed-sub(1, 2);
    say Renamed-sub(1, 2);
}

my $new = &Renamed-sub;
Renamed-sub(1, 2) + Renamed-sub(1, 2);