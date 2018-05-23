my @args = @*ARGS;

my @test-files = 't'.IO.dir;

for @test-files {
    run $*EXECUTABLE, @args, $_;
    say "===={$_.Str}";

    CATCH {
        default { .resume }
    }
}
