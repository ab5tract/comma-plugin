use JSON::Fast;
use Zef::Distribution;

sub MAIN($name, :$package) {
    my $module-name = $name.ends-with('.pm6') ?? $name.substr(0, *-4) !! $name;
    my @curs        = $*REPO.repo-chain.grep(*.?prefix.?e);
    my @repo-dirs   = @curs.map({.?prefix // .path-spec.?path}).map(*.IO);
    my @dist-dirs   = @repo-dirs.map(*.child('dist')).grep(*.e);
    my @dist-files  = @dist-dirs.map(*.IO.dir.grep(*.IO.f).Slip);

    my $dists := gather for @dist-files -> $file {
        if try { Zef::Distribution.new( |%(from-json($file.IO.slurp)) ) } -> $dist {
            next unless $dist.name eq $module-name || not $package;
            my $cur = @curs.first: {.prefix eq $file.parent.parent}
            take { :$dist, :from($cur) };
        }
    }

    # Some dists does not have version or api specified, so use dummy ones to
    # avoid warnings
    my @candis = $dists.sort({ $_<dist>.ver // v0 }).sort({ $_<dist>.api // 1 }).reverse;

    for @candis -> $candi {
        my $source-prefix = $candi<from>.prefix.child('sources');
        for $candi<dist>.provides -> $provides-item {
            if $package {
                for $provides-item.keys -> $impl {
                    for $provides-item{$impl}.values -> $value {
                        say "$impl=$source-prefix.child($value<file>)";
                    }
                }
            } else {
                with $provides-item{$module-name} {
                    say "$source-prefix.child($_.values[0]<file>)";
                    exit(0);
                }
            }
        }
    }
}
