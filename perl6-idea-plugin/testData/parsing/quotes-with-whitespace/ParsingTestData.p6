say "this is a test\n" ~~ rx { "this is a test" \n };
say ($ = "bar") ~~ tr /abc/def/;
say q 'foo';
say Qs 'baz $x';