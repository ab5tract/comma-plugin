my $foo = 5;
sub a(:$foo) {};
a(fo<caret>o => $foo);
