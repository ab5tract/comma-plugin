sub MAIN(
        Bool :$typegraph = False,
        Int  :$sparse,
        Bool :$disambiguation = True,
        Bool :$search-file = True,
        Bool :$no-highlight = False,
        Int  :$parallel = 1,
        ) {
    if !$no-highlight {
        if ! $coffee-exe.IO.f {
            say "Could not find $coffee-exe, did you run `make init-highlights`?";
            exit 1;
        }
        $proc = Proc::Async.new($coffee-exe, './highlights/highlight-filename-from-stdin.coffee', :r, :w);
        $proc-supply = $proc.stdout.lines;
    }
}