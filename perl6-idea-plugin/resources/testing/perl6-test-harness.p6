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

react {
    my $jobs = %*ENV<TEST_JOBS> // $*KERNEL.cpu-cores;
    run-a-test-file for ^$jobs;

    sub run-a-test-file {
        with @test-files.shift -> $file {
            my $proc = Proc::Async.new($*EXECUTABLE, @args, $file);
            my $output;
            whenever $proc {
                $output ~= $_;
            }
            whenever $proc.start -> $exit {
                say $output;
                say "====$file";
                try $*OUT.flush;
                run-a-test-file;
            }
        }
    }
}
