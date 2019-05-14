sub foo($pos1, :$named) {
    say ($named // "default").substr($pos1);
}

say ("One" // "default").substr(1);
