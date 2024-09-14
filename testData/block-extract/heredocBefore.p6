class Foo {
    method m() {
        say 42;
<selection>        say q:to/END/;
        blah blah blah
        END</selection>
        say 100;
    }
}