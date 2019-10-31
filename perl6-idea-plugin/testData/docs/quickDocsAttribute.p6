class Foo {
    .say;
    #| One
    #| Two
    #| Three!
    has $.foo = 42;
    #= Very important variable

    method a {
        $!foo<caret>;
    }
}