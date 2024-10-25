
use JSON::Fast;

sub MAIN() {
    my $repo = CompUnit::RepositoryRegistry.repository-for-name("core");

    my %path-lookup = |$repo.installed.map(-> $d {
       $d.meta<provides>.kv.map(-> $k,$v {
           $k => [ $d.content($v.keys.head).IO.absolute ]
       })
    }).flat;

    say to-json
    %(
        pathLookup => %path-lookup,
        notInstalled => []
    );
}
