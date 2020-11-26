multi m1($x) { $x }
multi m1($x, $y) { $x, $y }

sub test-multi() {
    <error descr="No multi candidates match (($): Not enough positional arguments, ($, $): Not enough positional arguments)">m1</error>();
}

sub slurpy($a, +@as) { $a, @as }
slurpy(1, 2, 3);

sub slurpy2(|c) {}
slurpy2(42, "anything", 42, "test", :test, :!testy);

class C {
    method no-args(C: ) {}
    method one-arg(C: $a) { $a }
    method two-args(C: $a, $b) { $a, $b}
}

sub test-class() {
    C.no-args(); C.no-args(<error descr="Too many positional arguments">1</error>); C.no-args(:a);
    C.one-arg(42); C<error descr="Not enough positional arguments">.one-arg()</error>; C.one-arg(1, <error descr="Too many positional arguments">2</error>); C.one-arg(1, :b);
    C.two-args(1, 2); C<error descr="Not enough positional arguments">.two-args()</error>; C.two-args(<error descr="Not enough positional arguments">1</error>); C.two-args(1, 2, <error descr="Too many positional arguments">3</error>);
}
