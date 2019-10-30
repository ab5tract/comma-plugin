my @args = @*ARGS;

constant TEST_HARNESS_PREFIX = 'TEST_HARNESS_PREFIX';

# Gather and sort test files.
my @test-files;
my @todo = 't'.IO;
while @todo {
    for @todo.pop.dir -> $path {
        @test-files.push($path) if !$path.d && $path.extension eq 't'|'t6'|'rakutest';
        @todo.push($path) if $path.d;
    }
}
@test-files .= sort;

# Check if we need to write coverage data.
my $coverage-dir;
with %*ENV<COMMA_TEST_COVERAGE> {
    $coverage-dir = .IO;
    $coverage-dir.mkdir;
}
my %coverage-index;

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

            my $ENV;
            my $cov-name;
            my $cov-file;
            if $coverage-dir {
                $cov-name = $file.subst(/\W+/, '-', :g);
                $cov-file = $coverage-dir.add($cov-name);
                $ENV := {
                    %*ENV,
                    MVM_SPESH_DISABLE => '1',
                    MVM_COVERAGE_LOG => $cov-file ~ '.%d'
                };
            }
            else {
                $ENV := %*ENV;
            }

            my $pid;
            whenever $proc.ready {
                $cov-file ~= ".$_" if $cov-file;
            }

            whenever $proc.start(:$ENV) -> $exit {
                say $output;
                say "{TEST_HARNESS_PREFIX} file $file";
                try $*OUT.flush;
                if $cov-file && $cov-file.IO.e {
                    %coverage-index{$file} = $cov-file;
                }
                elsif $cov-file && dir($coverage-dir, :test(/^$cov-name/)) -> @found {
                    %coverage-index{$file} = @found[0].absolute;
                }
                run-a-test-file;
            }
        }
    }
}

if %coverage-index {
    $coverage-dir.add('index').spurt: %coverage-index.fmt("%s\t%s");
}
