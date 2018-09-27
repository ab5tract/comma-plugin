class Foo {
    has $.public-attribute;

    method test-method() {
        $!public-attribute;
        $.public-attribute;
        self.public-attribute;
    }
}

Foo.new.public-attribute;
