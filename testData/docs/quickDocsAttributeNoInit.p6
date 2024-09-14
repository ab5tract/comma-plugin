class Foo {
    .say;
    #| One
    #| Two
    #| Three!
    has $.foo;
    #= Very important variable

    method a {
        $!foo<caret>;
    }
}