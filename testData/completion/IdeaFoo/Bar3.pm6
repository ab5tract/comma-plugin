use IdeaFoo::Baz;

class A does Outer::Baz3 {
    method a {
        say $<caret>
    }
}