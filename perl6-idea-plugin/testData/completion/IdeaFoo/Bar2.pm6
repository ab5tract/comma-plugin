use IdeaFoo::Baz;

class A does IdeaFoo::Bar::Baz2 {
    method a {
        say $<caret>
    }
}