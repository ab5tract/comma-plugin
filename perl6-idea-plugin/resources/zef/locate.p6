use JSON::Fast;
use Zef::Distribution;

my $module-name = @*ARGS[0];

my @curs       = $*REPO.repo-chain.grep(*.?prefix.?e);
my @repo-dirs  = @curs.map({.?prefix // .path-spec.?path}).map(*.IO);
my @dist-dirs  = @repo-dirs.map(*.child('dist')).grep(*.e);
my @dist-files = @dist-dirs.map(*.IO.dir.grep(*.IO.f).Slip);

my $dists := gather for @dist-files -> $file {
    if try { Zef::Distribution.new( |%(from-json($file.IO.slurp)) ) } -> $dist {
        next unless $dist.name eq $module-name;
        my $cur = @curs.first: {.prefix eq $file.parent.parent}
        take { :$dist, :from($cur) };
    }
}

my @candis = $dists.sort(*<dist>.ver).sort(*<dist>.api).reverse;

for @candis -> $candi {
    my $source-prefix = $candi<from>.prefix.child('sources');
    for $candi<dist>.provides -> $provides-item {
        for $provides-item.keys -> $impl {
            for $provides-item{$impl}.values -> $value {
                say "$impl=$source-prefix.child($value<file>)";
            }
        }
    }
}
