grammar fooNewType {}

class Foo {}

fooNewType;
fooNewType.new;
say fooNewType;
sub foo(fooNewType $a --> fooNewType) returns fooNewType {}
my fooNewType $foo;
subset Bar of fooNewType;
class Bar does fooNewType {}
grammar Bass is fooNewType {}
