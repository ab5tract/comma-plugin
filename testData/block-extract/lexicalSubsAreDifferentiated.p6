{
    my sub inner { say "You thought it was inner, but it was not!" }
    sub extracted(&inner) {
        say "Inner will be called:";
        inner;
    }
    sub long-sub {
        my sub inner { say "I am inner sub" }
        extracted(&inner);
    }
}