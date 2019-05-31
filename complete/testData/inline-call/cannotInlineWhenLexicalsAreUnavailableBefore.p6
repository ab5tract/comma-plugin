class A {
    my $lexical = True;

    method a {
        say $lexical;
    }
}

A.a<caret>;