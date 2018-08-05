my @args = @*ARGS;

my @test-files = 't'.IO.dir.grep(*.ends-with(".t"));

for @test-files {
    my $proc = run $*EXECUTABLE, @args, $_, :out, :merge;
    my $output = $proc.out.slurp: :close;
    say $output;
    LEAVE {
        say "===={$_.Str}";
        $*OUT.flush;
    }
}
