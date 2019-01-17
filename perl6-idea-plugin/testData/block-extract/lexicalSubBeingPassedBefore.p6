{
    my sub a($a) {
        say $a;
    }
    <selection>say 42;
    a(5);
    my sub b(@a) { say @a.reverse.join }
    b(<2 4>);
    say 24</selection>;
}