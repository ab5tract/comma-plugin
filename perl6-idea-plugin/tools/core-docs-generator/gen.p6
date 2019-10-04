use JSON::Fast;
use Pod::To::Text;
use Documentable;
use Documentable::Registry;
use Documentable::Config;
use Documentable::DocPage::Factory;

class CommaFactory is Documentable::DocPage::Factory {
    method generate-home-page() { %() }
    method generate-error-page() { %() }
    method generate-primary(Documentable::Primary $doc) {
      my $text = pod2text($doc.pod);
      my $type-doc = $text.split('Methods');
      my $synopsis = $type-doc[0];
#      my $rest = $type-doc[1];
#      with $rest {
          # $rest may contain Methods and Operators
#          my $methods = $rest.split(/^^ 'Operators' $$/)[0];

#          $rest = $rest.split(/^^ ['  method ' || '  routine ']/)>>.trim.grep(so *).List;
#      } else {
#          $rest = [];
#      }
      %( document => { :name($doc.name), desc => $synopsis } );
    }
    method generate-index(Kind $kind) { %() }
    method generate-subindex(Kind $kind, $category) { %() }
    method generate-search-file() { %() }
}

my $registry = Documentable::Registry.new(
    :!cache, topdir => 'doc/doc',
    dirs => ['Type'], :verbose
);

$registry.compose;

my $config = Documentable::Config.new(filename => 'config.json');

my $factory = CommaFactory.new(:$config, :$registry);

my @docs;

for $registry.documentables.kv -> $num, $doc {
    @docs.push($factory.generate-primary($doc));
}

my %docs;

for @docs.kv -> $num, $doc {
    %docs<types>.push: $doc<document>;
}

spurt 'result.json', to-json %docs;
