sub fo<caret>o($pos1, :$named) {
    say ($named // "default").substr($pos1);
}

foo(1, named => "One");
