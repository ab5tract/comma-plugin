my $a = 42;
sub evil() { return $a }
if True {
    my $a = 100;
    say ev<caret>il();
}