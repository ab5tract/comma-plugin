class B {...};
class A {
    trusts B;
    has $!foo;
    method !foo { return-rw $!foo }
    method perl { "A.new(foo => $!foo)" }
};
class B {
    has A $.a .= new;
    method change { $!a!A::foo = 42; self }
};
say B.new.change;