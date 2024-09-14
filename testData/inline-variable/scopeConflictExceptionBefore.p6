my $a = 42;
my $b = $a + 1;
if True {
    my $a = 'hides';
    say $<caret>b * 5;
}