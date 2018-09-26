class Foo {
    class Bartender {
        my $a = Foo::Bartender.new;
        my $b = Bartender.new;
        sub foo(Foo::Bartender $a, Bartender $b) {}
    }
}

say Foo::Bartender.new;