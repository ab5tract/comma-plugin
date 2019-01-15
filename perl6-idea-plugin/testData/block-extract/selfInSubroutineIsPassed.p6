sub foo($self) {
    say $self;
}
class {
    method uses-self {
        foo(self);
    }
}