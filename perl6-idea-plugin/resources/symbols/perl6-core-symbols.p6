use nqp;

for CORE::.keys {
    # Ignore a few things.
    when 'EXPORTHOW' | 'Rakudo' { }
    # Collect all top-level subs.
    when /^"&"<:Ll>/ { say "V:$_" }
    # Collect all types.
    when /^<:L>/ {
        output-package($_, CORE::{$_});
    }
    when '&EVAL' { say "V:$_" }
}

sub output-package($name, Mu \object) {
    # Emit but don't traverse concrete constants.
    if object.DEFINITE {
        say "D:$name";
    }
    else {
        # Emit anything that's type-like.
        if object.HOW.WHAT ~~ Metamodel::ClassHOW {
            say "C:$name";
            for object.^methods(:local) -> $m {
                if nqp::istype(nqp::decont($m), Method) != 0 {
                    say "{$m.name} : {$m.returns // 'Mu'}";
                } else {
                    say "{$m.name} : Mu";
                }
            }
        } elsif object.HOW.WHAT ~~ Metamodel::ParametricRoleGroupHOW {
            say "R:$name";
            for object.^methods(:local) -> $m {
                say "{$m.name} : {$m.returns // 'Mu'}";
            }
        } elsif object.HOW.WHAT ~~ Metamodel::PackageHOW ||
          object.HOW.WHAT =:= Metamodel::ModuleHOW {
            say "D:$name";
        }

        # Traverse children.
        try for object.WHO.keys {
            when /^<:L>/ {
                output-package($name ~ '::' ~ $_, object.WHO{$_});
            }
        }
    }
}
