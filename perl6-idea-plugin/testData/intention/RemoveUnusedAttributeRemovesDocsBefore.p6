class Test {
    has $.a;
    #| one
    #| two
    has $!unu<caret>sed is rw = 42;
    #= last
    #= last
    has $.b;
}
