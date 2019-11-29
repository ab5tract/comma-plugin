use JSON::Fast;

sub MAIN($name) {
    sub meta-by-module-name($name) {
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

        for @dist-files -> $file {
            my $meta = from-json $file.IO.slurp;

            next unless ($meta<name> // '') eq $module-name;
            (next unless ($meta<ver> // '') eq $_) with %params<ver>;
            (next unless ($meta<auth> // '') eq $_) with %params<auth>;

            return $meta<depends>;
        }
        return ();
    }

    my $visit = meta-by-module-name($name).Array;
    my $names;
    $names.append: |$visit;
    while $visit.pop -> $dep-to-add {
        $names.append: $dep-to-add;
        $visit.append: |meta-by-module-name($dep-to-add);
    }
    .say for @$names.unique.grep(*.defined);
}
