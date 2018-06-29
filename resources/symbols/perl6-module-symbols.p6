use MONKEY;

my %seen{Any};
my %seen-roles{Any};

EVAL "\{\n    @*ARGS[0] @*ARGS[1];\n" ~ Q:to/END/;
    # Emit explicit exports.
    my $INTERNAL_MY_KEYS_SEQUENCE = MY::.keys.map({$_ => True}).Hash;
    for MY::.kv -> $_, \object {
        # Ignore a few things.
        when '$INTERNAL_MY_KEYS_SEQUENCE' | '$_' | '$/' | '$!' | '&MONKEY-SEE-NO-EVAL' { }
        # Collect all top-level subs and variables
        when /^<[$@%&]><:L>/ { say 'V:' ~ $_ }
        # Collect all types.
        when /^<:L>/ {
            output-package($_, object, $INTERNAL_MY_KEYS_SEQUENCE);
        }
    }
    for (::(@*ARGS[1])::.WHO.kv) -> $_, \object {
        when /^<:L>/ {
            output-package($_, object, $INTERNAL_MY_KEYS_SEQUENCE);
        }
    }
}

sub describe-role(Mu \object) {
    say '!' ~ $_ for object.^candidates[0].^private_method_table.keys;
    say .name for object.^methods(:local);
    say .name for object.^attributes(:local);
}

sub resolve-lexical(Mu \object) {
    describe-role(object);
    for object.^roles -> $role {
        describe-role($role);
    }
}

sub resolve-roles($name, Mu \object, $keys) {
    # Resolve implemented roles
    for object.^roles -> \r {
        if $keys{r.^name} {
            # Our-scoped role
            if %seen-roles{r} {
                say 'DOES:' ~ r.^name;
            } else {
                say 'DOES NEXT';
                output-package(r.^name, r, $keys);
                %seen-roles{r} = 1;
            }
        } elsif CORE::{r.^name} === r {
            # Role from CORE
            # FIXME when we need to complete methods
        } else {
            # Lexical role
            resolve-lexical(r);
        }
    }
}

sub output-package($name, Mu \object, $keys) {
    # Emit but don't traverse concrete constants.
    # Also, if we've seen the object before,
    # don't recurse any deeper into it.
    # (this can happen for example with constants used to
    # make aliases to longer types)
    if object.DEFINITE || %seen{object} {
        say 'D:' ~ $name unless $name ~~ /'EXPORT::' .+? '&'/;
    }
    elsif %seen-roles{object} { return }
    else {
        # Emit anything that's type-like.
        if object.HOW.WHAT =:= Metamodel::PackageHOW
        || object.HOW.WHAT =:= Metamodel::ModuleHOW {
            for object::.keys -> $key {
                output-package($name ~ '::' ~ $key, (object::{$key}), $keys);
            }
        } else {
            say 'D:' ~ $name;
            if object.HOW.WHAT =:= Metamodel::EnumHOW {
                say 'E:' ~ $name;
            } elsif object.HOW.WHAT =:= Metamodel::SubsetHOW {
                say 'S:' ~ $name;
            } elsif object.HOW.WHAT =:= Metamodel::ClassHOW {
                say 'C:' ~ $name;
            } elsif object.HOW.WHAT =:= Metamodel::ParametricRoleGroupHOW {
                say 'R:' ~ $name;
                describe-role(object);
                resolve-roles($name, object, $keys) if (object.^roles.elems != 0);
                %seen-roles{object} = 1;
            }
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
