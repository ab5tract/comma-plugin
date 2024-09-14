{
    my sub inner { say "You thought it was inner, but it was not!" }
    sub long-sub {
        my sub inner { say "I am inner sub" }
        <selection>say "Inner will be called:";
        inner</selection>;
    }
}