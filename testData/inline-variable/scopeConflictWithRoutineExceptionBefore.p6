my $a = 42;
my $b = $a + evil();
if True {
    say $<caret>b;
    sub evil() { 2 }
}
sub evil() { 1 }