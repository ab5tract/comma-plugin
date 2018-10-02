class Foo {
    has $.renamed-attribute;

    method test-method() {
        $!renamed-attribute;
        $.renamed-attribute;
        self.renamed-attribute;
    }
}

Foo.new.renamed-attribute;
