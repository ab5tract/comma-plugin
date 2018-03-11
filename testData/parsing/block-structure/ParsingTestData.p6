my $a = { say 42 };
my $x = -> $a { say 42 };
sub foo() {
    if $x -> $y {
    }
    else {
    }
}
class Foo {
    method bar() {
        LEAVE {
            say "bye";
        }
    }
}
my token bar { a { $x } b }
say "some { "interpolation" }";