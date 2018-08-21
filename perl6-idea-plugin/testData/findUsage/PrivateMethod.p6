class Test {
    method !foo {
        self!foo;
        self!foo;
        self!foo;
    }

    method test {
        self!f<caret>oo;
    }
}