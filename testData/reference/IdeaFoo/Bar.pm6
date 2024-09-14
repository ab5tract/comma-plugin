class Foo {}

my $my-variable = 5;
my $exported-variable is export = 5;

sub exported-sub() is export {}

sub not-exported-sub() {}
