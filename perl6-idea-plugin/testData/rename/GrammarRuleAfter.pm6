grammar Foo {
    rule foo-bar { <?> }
    rule bar { <foo-bar> }
}
