sub foo($pos1, :$named) {
    say ($named // "default").substr($pos1);
}

f<caret>oo(1, named => "One");
