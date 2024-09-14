sub foo(:$named1, :$named2) {
    say "First was $named1";
    say "Second was $named2";
}

do {
    say "First was "First"";
    say "Second was "Second"";
};
