sub fo<caret>o(:$named1, :$named2) {
    say "First was $named1";
    say "Second was $named2";
}

foo(named2 => "Second", named1 => "First");
