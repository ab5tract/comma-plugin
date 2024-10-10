use JSON::Fast;

sub MAIN(**@modules) {

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
            my $json = $file.IO.slurp;
            next unless $json.contains($module-name);
            my $meta = from-json $json;

            next unless ($meta<name> // '') eq $module-name;
            (next unless (Version.new($meta<ver> // '')) ~~ $_) with %params<ver>;
            (next unless ($meta<auth> // '') eq $_) with %params<auth>;

            # TODO: Make this more robust. So far this only affects File::Which
            with $meta<depends>[0]<name><by-distro.name><mswin32> -> $platform-depends {
                if $*DISTRO.is-win {
                    return [ $platform-depends ]
                } else {
                    next
                }
            }

            return $meta<depends>;
        }
        return ();
    }

    my @visit;
    for @modules -> $module {
        @visit.push: |meta-by-module-name($module);
    }

    my @names .= append: |@visit;
    my %visited;
    while @visit.pop -> $dep-to-add {
        next if %visited{$dep-to-add}++;
        @names.append: $dep-to-add;
        @visit.append: |meta-by-module-name($dep-to-add);
    }
    .say for @names.unique.grep(*.defined);
}
