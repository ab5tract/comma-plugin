use MONKEY;

my %seen{Mu};

EVAL "\{\n    @*ARGS[0] @*ARGS[1];\n" ~ Q:to/END/;
    for MY::.kv -> $_, \object {
        # Ignore a few things.
        when '$_' | '$/' | '$!' | '&MONKEY-SEE-NO-EVAL' { }
        # Collect all top-level subs and variables
        when /^<[$@%&]><:L>/ { say 'V:' ~ $_ }
        # Collect all types.
        when /^<:L>/ {
            output-package($_, object);
        }
    }
}

sub describe-role(Mu \object) {
    for object, |object.^roles {
        say "!$_" for .^candidates[0].^private_method_table.keys;
        for .^methods(:local) -> $m {
            say "{$m.name} : {$m.returns // 'Mu'}";
        }
        say .name for .^attributes(:local);
    }
}

sub describe-class(Mu \object) {
    use nqp;
    # Inner stuff of class: PRIVATE METHODS
    for object, |object.^parents -> $class { # From self and parents
        say "!$_" for $class.^private_method_table.keys;
    }
    for object.^roles -> $role { # From roles
        say "!$_" for $role.^candidates[0].^private_method_table.keys;
    }
    # METHODS
    for object.^methods(:local) -> $m {
        if nqp::istype(nqp::decont($m), Method) != 0 {
            say "{$m.name} : {$m.returns // 'Mu'}";
        } else {
            say "{$m.name} : Mu";
        }
    }
    # ATTRIBUTES
    say .name for object.^attributes;
}

sub describer($name, Mu \object) {
    # Emit native types
    if object.HOW.WHAT ~~ Metamodel::NativeHOW {
        say "D:$name";
    } elsif object.HOW.WHAT ~~ Metamodel::ClassHOW { # Emit classes
        say "C:$name";
        describe-class(object);
    } elsif object.HOW.WHAT ~~ Metamodel::ParametricRoleGroupHOW { # Emit roles
        say "R:$name";
        describe-role(object);
    }
}

sub output-package($name, Mu \object) {
    # To avoid weird `C:NativeCall::EXPORT::ALL::&postcircumfix:<[ ]>` like things
    return if object ~~ Sub;
    # Emit but don't traverse concrete constants.
    # Also, if we've seen the object before,
    # don't recurse any deeper into it.
    # (this can happen for example with constants used to
    # make aliases to longer types)
    if object.DEFINITE || %seen{object} {
        describer($name, object);
    }
    else {
        describer($name, object);
        %seen{object} = 1;

        # Traverse children.
        try for object.WHO.keys {
            when /^<:L>/ {
                try output-package("$name\::$_", object.WHO{$_});
            }
        }
    }
}
END
