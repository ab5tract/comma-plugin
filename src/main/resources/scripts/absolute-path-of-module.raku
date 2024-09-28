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

my $config = Zef::Config::parse-file(Zef::Config::guess-path());
my $client = Zef::Client.new(:$config);

#sub resolve-to-path(Str $identity) {
#    my @candis = $client.list-installed.grep({
#        .dist.meta<provides>.keys.grep({ parse-value($_) eq $identity }).so;
#    });
#
#    my %lookup;
#    for @candis -> $candi {
#        if $candi {
#            # This is relying on implementation details for compatibility purposes. It will
#            # use something more appropriate sometime in 2019.
#            my %meta = $candi.dist.meta;
#            %meta<provides> = %meta<provides>.map({ $_.key => parse-value($_.value) }).hash;
#            my $lib = %meta<provides>{$identity};
#            my $lib-sha1 = nqp::sha1(CompUnit::Repository::Distribution.new($candi.dist).id);
#            my $curi = CompUnit::RepositoryRegistry.repository-for-spec($candi.from);
#
#            %lookup{$identity} = $curi.prefix.child('sources').child($lib-sha1).Str;
#        }
#    }
#    %lookup
#}

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

    for @identities -> $identity {
        %full-lookup.push: resolve-to-path($identity);
    }

    for %full-lookup.pairs -> (:$key, :$value) {
        %full-lookup{$key} = [$value]
    }

    say to-json(%full-lookup)
}

