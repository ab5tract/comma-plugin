class Bar {
    method a {
        my $a;
        my $b;
        self!mm<caret>m(:$a, :b($b), :42c, :d, :!e, :$<f>);
    }
}