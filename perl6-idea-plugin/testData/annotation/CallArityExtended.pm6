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
