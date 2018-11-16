use Test;

plan *;

constant $HARNESS-SCRIPT = '../../resources/testing/perl6-test-harness.p6';

my $proc = run $*EXECUTABLE, $HARNESS-SCRIPT, :out;
my $output = $proc.out.slurp(:close);
like $output, / 'ok 1 - a' /;
like $output, / 'ok 1 - b' /;
like $output, / 'ok 1 - Nested'/;

done-testing;
