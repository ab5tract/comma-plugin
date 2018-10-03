role Foo {
    method !bar {
        self!bar;
    }
}

class Test does Foo {
    method !bar {
        self!bar;
        self!ba<caret>r;
    }
}