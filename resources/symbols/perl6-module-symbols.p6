use MONKEY;

my %seen{Any};

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
    # Also, if we've seen the object before,
    # don't recurse any deeper into it.
    # (this can happen for example with constants used to
    # make aliases to longer types)
    if object.DEFINITE || %seen{object} {
        say $name;
    }
    else {
        # Emit anything that's type-like.
        unless object.HOW.WHAT =:= Metamodel::PackageHOW
                || object.HOW.WHAT =:= Metamodel::ModuleHOW {
            say $name;
        }

        %seen{object} = 1;

        # Traverse children.
        try for object.WHO.keys {
            when /^<:L>/ {
                output-package($name ~ '::' ~ $_, object.WHO{$_});
            }
        }
    }
}
END
