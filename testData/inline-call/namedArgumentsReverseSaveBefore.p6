sub foo(:$named1, :$named2) {
    say "First was $named1";
    say "Second was $named2";
}

f<caret>oo(named2 => "Second", named1 => "First");
