use MONKEY;

my %seen{Any};

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
        say .name for .^methods(:local);
        say .name for .^attributes(:local);
    }
}

sub describe-class(Mu \object) {
    # Inner stuff of class: PRIVATE METHODS
    for object, |object.^parents -> $class { # From self and parents
        say "!$_" for $class.^private_method_table.keys;
    }
    for object.^roles -> $role { # From roles
        say "!$_" for $role.^candidates[0].^private_method_table.keys;
    }
    # METHODS
    say .name for object.^methods;
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
    } elsif object.HOW.WHAT ~~ Metamodel::ModuleHOW || # Emit module
      object.HOW.WHAT ~~ Metamodel::PackageHOW { # or package content
        output-package("$name\::$_", (object::{$_})) for object::.keys;
    } elsif object.HOW.WHAT ~~ Metamodel::ParametricRoleGroupHOW { # Emit roles
        say "R:$name";
        describe-role(object);
    }
}

sub output-package($name, Mu \object) {
    # To aboid weird `C:NativeCall::EXPORT::ALL::&postcircumfix:<[ ]>` like things
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
                output-package("$name\::$_", object.WHO{$_});
            }
        }
    }
}
END
