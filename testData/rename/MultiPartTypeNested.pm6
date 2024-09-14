class Foo {
    class Bar {
        my $a = Foo::Bar.new;
        my $b = Bar.new;
        sub foo(Foo::Bar $a, Bar $b) {}
    }
}

say Foo::Bar.new;