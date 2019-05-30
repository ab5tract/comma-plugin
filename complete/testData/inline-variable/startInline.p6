my $foo = start { 42 };
my $two = await (start { 42 });