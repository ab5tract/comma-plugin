my @foo = 5;

say @foo;
so @foo.grep(*.defined).all;