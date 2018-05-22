use MONKEY;
EVAL "\{\n    @*ARGS[0] @*ARGS[1];\n" ~ Q:to/END/;
    # Emit explicit exports.
    for MY::.kv -> $_, \object {
        # Ignore a few things.
        when '$_' | '$/' | '$!' | '&MONKEY-SEE-NO-EVAL' { }
        # Collect all top-level subs and variables
        when /^<[$@%&]><:L>/ { .say }
        # Collect all types.
        when /^<:L>/ {
            output-package($_, object);
        }
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
END
