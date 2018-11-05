class Bar {
    method a {
        my $a;
        my $b;
        self!mmm(:$a, :b($b), :42c, :d, :!e, :$<f>);
    }
    method !mmm(:$a, :$b, :$c, :$d, :$e, :$f) {

    }
}