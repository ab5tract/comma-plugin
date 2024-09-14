class Bar {
    method a {
        my $a;
        my $b;
        '' ~~ / $<f> = \w /;
        self!mm<caret>m(:$a, :b($b), :42c, :d, :!e, :$<f>);
    }
}