class Foo {
    method m() {
        say 42;
        sub heredoc() {
            say q:to/END/;
                    blah blah blah
        END

        }
        heredoc();
        say 100;
    }
}