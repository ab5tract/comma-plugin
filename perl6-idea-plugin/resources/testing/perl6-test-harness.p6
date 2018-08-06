my @args = @*ARGS;

my @test-files;
my @todo = 't'.IO;

while @todo {
    for @todo.pop.dir -> $path {
        @test-files.push($path) if !$path.d && $path.extension eq 't';
        @todo.push($path) if $path.d;
    }
}

@test-files .= sort;

for @test-files {
    my $proc = run $*EXECUTABLE, @args, $_, :out, :merge;
    my $output = $proc.out.slurp: :close;
    say $output;
    LEAVE {
        say "===={$_.Str}";
        $*OUT.flush;
    }
}