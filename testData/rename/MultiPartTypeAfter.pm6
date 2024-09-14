grammar Foo::Baz {}

class Foo::Bar {}

Foo::Baz;
Foo::Baz.new;
say Foo::Baz;
sub foo(Foo::Baz $a --> Foo::Baz) returns Foo::Baz {}
my Foo::Baz $foo;
subset Bar of Foo::Baz;
class Bar does Foo::Baz {}
grammar Bass is Foo::Baz {}
