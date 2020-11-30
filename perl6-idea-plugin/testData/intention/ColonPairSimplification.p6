my $foo = 5;
sub a(:$foo) {};
a(:$foo);
