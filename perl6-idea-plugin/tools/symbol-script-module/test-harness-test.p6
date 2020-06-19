use Test;

plan *;

constant $HARNESS-SCRIPT = '../../resources/testing/perl6-test-harness.p6';

# two paths used are a horrible workaround, as older rakudo doesn't understand a single argument being matched as Positional
my $proc = run $*EXECUTABLE, $HARNESS-SCRIPT, '--paths=.', '--paths=lib', :out;
my $output = $proc.out.slurp(:close);
like $output, / 'ok 1 - a' /;
like $output, / 'ok 1 - b' /;
like $output, / 'ok 1 - Nested'/;

done-testing;
