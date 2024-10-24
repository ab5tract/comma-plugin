#!/usr/bin/env raku
use v6.d;

use nqp;
use Zef::Client;
use Zef::Config;
use JSON::Fast;

my sub parse-value($str-or-kv) {
    do given $str-or-kv {
        when Str  { $_ }
        when Hash { $_.keys[0] }
        when Pair { $_.key     }
    }
}
#BEGIN die Zef::Config::guess-path();
my $config = Zef::Config::parse-file(Zef::Config::guess-path());
my $client = Zef::Client.new(:$config);

sub resolve-to-path(Str $identity) {
    my @candis = $client.list-installed.grep({
        .dist.meta<provides>.keys.grep({ parse-value($_) eq $identity }).so;
    });

    my %lookup;
    for @candis -> $candi {
        if $candi {
            my $curi = CompUnit::RepositoryRegistry.repository-for-spec($candi.from);
            my $source-prefix = $curi.prefix.child('sources');
            my $source-path   = $source-prefix.child(nqp::sha1($identity ~ CompUnit::Repository::Distribution.new($candi.dist).id));
            %lookup{$identity} = ~$source-path if $source-path.IO.f;
        }
    }
    %lookup
}

sub MAIN(*@identities) {
    my %full-lookup;
    my @not-installed;

    for @identities -> $identity {
        if resolve-to-path($identity) -> %resolved {
            %full-lookup.push: %resolved
        } else {
            @not-installed.push: $identity
        }
    }

    for %full-lookup.pairs -> (:$key, :$value) {
        %full-lookup{$key} = [$value]
    }

    my %output = %(
        notInstalled => @not-installed,
        pathLookup => %full-lookup
    );

    say to-json(%output)
}

