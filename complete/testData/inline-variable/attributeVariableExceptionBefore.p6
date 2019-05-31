class Foo {
    has $!foo;
    has $.bar;

    method foo {
        my $pair = $!foo + $!bar;

        class Bar {
            method a {
                say $pa<caret>ir;
            }
        }
        Bar.a;
    }
}