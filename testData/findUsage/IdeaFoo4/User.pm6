use IdeaFoo4::Base;

my Test $test;

sub foo1(Test $parameter) {}

sub foo2(--> Test) {}

sub foo3 returns Test {}

subset TestSubset of Test;

class A does Test {}
class B is Test {}
role C does Test {}
role D is Test {}