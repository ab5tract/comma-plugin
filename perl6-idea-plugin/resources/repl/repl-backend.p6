# Script used to back the Comma REPL support. It does largely what
# the real REPL does: build up a set of nested closures. It receives
# commands to execute, runs them, and produces the output. The commands
# are in an envelope of the form "EVAL <number-of-lines>\n" so we can
# handle multi-line inputs easily.

use MONKEY;

# Disable any output buffering.
$*OUT.out-buffer = 0;
$*ERR.out-buffer = 0;

my $*NEXT-EVAL = -> $code { EVAL $code };
loop {
    my $command = $*IN.get;
    given $command {
        when /^'EVAL '(\d+)$/ {
            my @lines = (^+$0).map({ $*IN.get });
            my $code = '{ use MONKEY; $*NEXT-EVAL = -> $code { EVAL $code }; ' ~
                @lines.join("\n") ~ '}';
            my $output-pos = $*OUT.tell;
            my $result = $*NEXT-EVAL($code);
            if $*OUT.tell == $output-pos {
                say $result;
            }
            CATCH {
                default {
                    .note;
                }
            }
        }
    }
}
