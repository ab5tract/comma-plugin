use JSON::Fast;

sub MAIN($name) {
    my $module-name = $name.ends-with('.pm6') ?? $name.substr(0, *-4) !! $name;
    my @curs        = $*REPO.repo-chain.grep(*.?prefix.?e);
    my @repo-dirs   = @curs.map({.?prefix // .path-spec.?path}).map(*.IO);
    my @dist-dirs   = @repo-dirs.map(*.child('dist')).grep(*.e);
    my @dist-files  = @dist-dirs.map(*.IO.dir.grep(*.IO.f).Slip);

    my %params;

    with $module-name ~~ / ^ (.+?\w) (':' \w<-[:]>+)* $ / {
        $module-name = $0;
        for @$1 {
            if $_.Str.starts-with(':ver') {
                %params<ver> = Version.new($_.Str.substr(5, *-1));
            } elsif $_.Str.starts-with(':auth') {
                %params<auth> = $_.Str.substr(6, *-1);
            }
        }
    }

    my $dists := gather for @dist-files -> $file {
        my $dist = try from-json($file.IO.slurp);
        with $dist {
            next unless $dist<name> eq $module-name;
            (next unless $dist<ver> eq $_) with %params<ver>;
            (next unless $dist<auth> eq $_) with %params<auth>;

            my $cur = @curs.first: {.prefix eq $file.parent.parent}
            take { :$dist, :from($cur) };
        }
    }

    # Some dists does not have version or api specified, so use dummy ones to
    # avoid warnings
    my @candis = $dists.sort({ $_<dist><ver> // v0 }).sort({ $_<dist><api> // 1 }).reverse;



    for @candis.first -> $candi {
        my $source-prefix = $candi<from>.prefix.child('sources');
        for $candi<dist><provides> -> $provides-item {
            for $provides-item.keys -> $impl {
               for $provides-item{$impl}.values -> $value {
                    say "$impl=$source-prefix.child($value<file>)";
                }
            }
        }
    }
}
