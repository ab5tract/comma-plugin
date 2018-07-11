for CORE::.keys {
    # Ignore a few things.
    when 'EXPORTHOW' | 'Rakudo' { }
    # Collect all top-level subs.
    when /^"&"<:Ll>/ { .say }
    # Collect all types.
    when /^<:L>/ {
        output-package($_, CORE::{$_});
    }
}

sub output-package($name, Mu \object) {
    # Emit but don't traverse concrete constants.
    if object.DEFINITE {
        say $name;
    }
    else {
        # Emit anything that's type-like.
        unless object.HOW.WHAT =:= Metamodel::PackageHOW
                || object.HOW.WHAT =:= Metamodel::ModuleHOW {
            say $name;
        }

        # Traverse children.
        try for object.WHO.keys {
            when /^<:L>/ {
                output-package($name ~ '::' ~ $_, object.WHO{$_});
            }
        }
    }
}
