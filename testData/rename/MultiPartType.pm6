grammar Foo::Bar {}

class Foo::Bar {}

Foo::Bar;
Foo::Bar.new;
say Foo::Bar;
sub foo(Foo::Bar $a --> Foo::Bar) returns Foo::Bar {}
my Foo::Bar $foo;
subset Bar of Foo::Bar;
class Bar does Foo::Bar {}
grammar Bass is Foo::Bar {}
