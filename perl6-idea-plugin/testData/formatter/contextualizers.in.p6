my $starter;

my $a = %(:a42);

%(
        (do "heh"), (do 42),
                (do "WOW"), (do 54);
        );

my @ender;

@(
        do if True { 42 } else { 'NOPE' }
        q:to/ENDE/;
        one two
three four
ENDE
);

my $a = @(:a42);