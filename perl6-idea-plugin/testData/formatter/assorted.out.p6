sub foo($a, $b, $c,
        $d, $e, $f) {
    if $x == 42 {
        say 1;
        say 5 +
                40
                + 21;
        say 2
                + (42 +
                5);
        if $y < 10 {
        }
        else {
            say "bye";
        }
        say Foo.new:
                a => 1,
                b => 2;

        say Wat.new:
                blah => 1,
                blaah => 2,
                da => 42;

        say Bar.new:
                a => 1,
                b => 2;

        return {
            Foo.new:
                    a => 1,
                    b => 2
        }
    }
}

if $x {

}
else {
    say 100;
}

my $x = Foo.new:
        a => 1;

is-deeply %foo,
        { a => 1 },
        'bar';

is-deeply %foo,
        { xxx => 1 },
        'blah';

is-deeply %bar,
        { a => 1 },
        'blah';

is-deeply %foo,
        { a => 1 },
        'bar';

say Foo.new:
        a => 1,
        c => 42,
        b => 2;

blah {
    say 1
}

if $x {

}
else {
    say 42;
}

done-testing;
