use IdeaFoo::Baz;

class A does Baz1 {
    method a {
        self!<caret>;
    }
}