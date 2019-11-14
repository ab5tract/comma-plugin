my $module-name = @*ARGS[0];

my $proc = run 'zef', 'locate', $module-name, :out;

my $res = $proc.out.slurp;

say ($res ~~ / $module-name \s* '=>' \s+? (.+) "\n" /)[0].Str.trim;