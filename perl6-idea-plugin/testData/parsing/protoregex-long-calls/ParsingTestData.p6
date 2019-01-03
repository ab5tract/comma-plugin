grammar G {
    proto regex foo {*}
    regex foo:a { a }
    proto regex bar {*}
    regex bar:sym<b> { b }

    token TOP { <foo:a> <bar:sym<b>> }
}