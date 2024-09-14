grammar FooNewType {}

class Foo {}

FooNewType;
FooNewType.new;
say FooNewType;
sub foo(FooNewType $a --> FooNewType) returns FooNewType {}
my FooNewType $foo;
subset Bar of FooNewType;
class Bar does FooNewType {}
grammar Bass is FooNewType {}
