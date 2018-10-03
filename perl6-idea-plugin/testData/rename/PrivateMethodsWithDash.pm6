class Foo {
    method !private-one() {
        self!private-one;
        self!private-one;
    }

    method !another-private() {
        self!private-one;
    }
}