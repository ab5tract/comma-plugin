use nqp;

# ========== JSON CODE FROM JSON::Fast ==========

multi sub to-surrogate-pair(Int $ord) {
    my int $base   = $ord - 0x10000;
    my int $top    = $base +& 0b1_1111_1111_1100_0000_0000 +> 10;
    my int $bottom = $base +&               0b11_1111_1111;
    Q/\u/ ~ (0xD800 + $top).base(16) ~ Q/\u/ ~ (0xDC00 + $bottom).base(16);
}

multi sub to-surrogate-pair(Str $input) {
    to-surrogate-pair(nqp::ordat($input, 0));
}

my $tab := nqp::list_i(92,116); # \t
my $lf  := nqp::list_i(92,110); # \n
my $cr  := nqp::list_i(92,114); # \r
my $qq  := nqp::list_i(92, 34); # \"
my $bs := nqp::list_i(92, 92); # \\

my sub str-escape(\text) {
    my $codes := text.NFD;
    my int $i = -1;

    nqp::while(
      nqp::islt_i(++$i,nqp::elems($codes)),
      nqp::if(
        nqp::isle_i((my int $code = nqp::atpos_i($codes,$i)),92)
          || nqp::isge_i($code,128),
        nqp::if(                                           # not ascii
          nqp::isle_i($code,31),
          nqp::if(                                          # control
            nqp::iseq_i($code,10),
            nqp::splice($codes,$lf,$i++,1),                  # \n
            nqp::if(
              nqp::iseq_i($code,13),
              nqp::splice($codes,$cr,$i++,1),                 # \r
              nqp::if(
                nqp::iseq_i($code,9),
                nqp::splice($codes,$tab,$i++,1),               # \t
                nqp::stmts(                                    # other control
                  nqp::splice($codes,$code.fmt(Q/\u%04x/).NFD,$i,1),
                  ($i = nqp::add_i($i,5))
                )
              )
            )
          ),
          nqp::if(                                          # not control
            nqp::iseq_i($code,34),
            nqp::splice($codes,$qq,$i++,1),                  # "
            nqp::if(
              nqp::iseq_i($code,92),
              nqp::splice($codes,$bs,$i++,1),                 # \
              nqp::if(
                nqp::isge_i($code,0x10000),
                nqp::stmts(                                    # surrogates
                  nqp::splice(
                    $codes,
                    (my $surrogate := to-surrogate-pair($code.chr).NFD),
                    $i,
                    1
                  ),
                  ($i = nqp::sub_i(nqp::add_i($i,nqp::elems($surrogate)),1))
                )
              )
            )
          )
        )
      )
    );

    nqp::strfromcodes($codes)
}

my sub to-json(
  \obj,
  Int  :$level          = 0,
  int  :$spacing        = 2,
  Bool :$sorted-keys    = False,
  Bool :$enums-as-value = False,
) {

    my str @out;
    my str $spaces = ' ' x $spacing;
    my str $comma  = ",\n" ~ $spaces x $level;

#-- helper subs from here, with visibility to the above lexicals
    sub unpretty-positional(\positional --> Nil) {
        nqp::push_s(@out,'[');
        my int $before = nqp::elems(@out);
        for positional.list {
            jsonify($_);
            nqp::push_s(@out,",");
        }
        nqp::pop_s(@out) if nqp::elems(@out) > $before;  # lose last comma
        nqp::push_s(@out,']');
    }

    sub unpretty-associative(\associative --> Nil) {
        nqp::push_s(@out,'{');
        my \pairs := $sorted-keys
          ?? associative.sort(*.key)
          !! associative.list;

        my int $before = nqp::elems(@out);
        for pairs {
            jsonify(.key);
            nqp::push_s(@out,":");
            jsonify(.value);
            nqp::push_s(@out,",");
        }
        nqp::pop_s(@out) if nqp::elems(@out) > $before;  # lose last comma
        nqp::push_s(@out,'}');
    }

    sub jsonify(\obj --> Nil) {

        with obj {

            # basic ones
            if nqp::istype($_, Bool) {
                nqp::push_s(@out,obj ?? "true" !! "false");
            }
            elsif nqp::istype($_, IntStr) {
                jsonify(.Int);
            }
            elsif nqp::istype($_, RatStr) {
                jsonify(.Rat);
            }
            elsif nqp::istype($_, NumStr) {
                jsonify(.Num);
            }
            elsif nqp::istype($_, Enumeration) {
                if $enums-as-value {
                    jsonify(.value);
                }
                else {
                    nqp::push_s(@out,'"');
                    nqp::push_s(@out,str-escape(.key));
                    nqp::push_s(@out,'"');
                }
            }
            # Str and Int go below Enumeration, because there
            # are both Str-typed enums and Int-typed enums
            elsif nqp::istype($_, Str) {
                nqp::push_s(@out,'"');
                nqp::push_s(@out,str-escape($_));
                nqp::push_s(@out,'"');
            }

            # numeric ones
            elsif nqp::istype($_, Int) {
                nqp::push_s(@out,.Str);
            }
            elsif nqp::istype($_, Rat) {
                nqp::push_s(@out,.contains(".") ?? $_ !! "$_.0")
                  given .Str;
            }
            elsif nqp::istype($_, FatRat) {
                nqp::push_s(@out,.contains(".") ?? $_ !! "$_.0")
                  given .Str;
            }
            elsif nqp::istype($_, Num) {
                if nqp::isnanorinf($_) {
                    nqp::push_s(
                      @out,
                      $*JSON_NAN_INF_SUPPORT ?? obj.Str !! "null"
                    );
                }
                else {
                    nqp::push_s(@out,.contains("e") ?? $_ !! $_ ~ "e0")
                      given .Str;
                }
            }

            # iterating ones
            elsif nqp::istype($_, Seq) {
                jsonify(.cache);
            }
            elsif nqp::istype($_, Positional) {
                  unpretty-positional($_);
            }
            elsif nqp::istype($_, Associative) {
                  unpretty-associative($_);
            }

            # rarer ones
            elsif nqp::istype($_, Dateish) {
                nqp::push_s(@out,qq/"$_"/);
            }
            elsif nqp::istype($_, Instant) {
                nqp::push_s(@out,qq/"{.DateTime}"/);
            }
            elsif nqp::istype($_, Version) {
                jsonify(.Str);
            }

            # huh, what?
            else {
                die "Don't know how to jsonify {.^name}";
            }
        }
        else {
            nqp::push_s(@out,'null');
        }
    }

#-- do the actual work

    jsonify(obj);
    nqp::join("",@out)
}

# ========== END OF JSON CODE ==========

my @EXTERNAL_COMMA_ELEMS;

for CORE::.keys -> $_ {
    # Ignore a few things.
    when 'EXPORTHOW' | 'Rakudo' { }
    # Collect all top-level subs and EVAL.
    when /^"&"<:Ll>/ | '&EVAL' {
        @EXTERNAL_COMMA_ELEMS.push: pack-variable($_, CORE::{$_});
        if ($_.starts-with('&')) {
            if CORE::{$_}.candidates.elems > 1 {
                @EXTERNAL_COMMA_ELEMS.push: pack-code(CORE::{$_}, 1, :!is-method);
                @EXTERNAL_COMMA_ELEMS.push: pack-code($_, 2, :!is-method) for CORE::{$_}.candidates;
            } else {
                @EXTERNAL_COMMA_ELEMS.push: pack-code(CORE::{$_}, 0, :!is-method);
            }
        }
    }
    # Collect all types.
    when /^<:L>/ {
        pack-package(@EXTERNAL_COMMA_ELEMS, $_, CORE::{$_});
    }
}

put to-json @EXTERNAL_COMMA_ELEMS;

sub pack-variable($name, \object, :$is-attribute = False) {
    %( k => "v", n => $name, t => $is-attribute ?? object.type.^name !! object.^name );
}

sub pack-code($code, Int $multiness, Str $name?, :$is-method) {
    my $s = $code.signature;
    my @params = $s.params;
    @params .= skip(1) if $is-method;
    my @parameters = @params.map(*.gist).List;
    my %signature = r => $s.returns.^name, p => @parameters;
    my $kind = $code.^name.comb.head.lc;
    %( k => $kind, n => $name // $code.name, s => %signature, m => $multiness );
}

sub pack-package(@elems, $name, Mu \object) {
    describer(@elems, $name, object);
    # Don't traverse concrete constants
    unless object.DEFINITE {
        try for object.WHO.keys {
            when /^<:L>/ {
                pack-package(@elems, $name ~ '::' ~ $_, object.WHO{$_});
            }
        }
    }
}

sub describer(@elems, $name, Mu \object) {
    if object.HOW.WHAT ~~ Metamodel::NativeHOW {
        @elems.push: %( k => "n", n => $name, t => object.^name );
    } elsif object.HOW.WHAT ~~ Metamodel::ClassHOW {
        describe-OOP(@elems, $name, "c", object);
    } elsif object.HOW.WHAT ~~ Metamodel::ParametricRoleGroupHOW {
        describe-OOP(@elems, $name, "ro", object);
    } elsif object.HOW.WHAT ~~ Metamodel::EnumHOW {
        @elems.push: %( k => "e", n => $name, t => object.^name );
    } elsif object.HOW.WHAT ~~ Metamodel::SubsetHOW {
        @elems.push: %( k => "ss", n => $name, t => object.^name, b => object.^refinee.^name );
    }
}

sub describe-OOP(@elems, $name, $kind, Mu \object) {
    my %class = k => $kind, n => $name, t => object.^name;
    my @privates;
    if $kind eq "ro" {
        @privates = object.^candidates[0].^private_method_table.values;
    } else {
        @privates = object.^private_method_table.values;
    }
    for object.^methods -> $method {
        try {
            if $method.candidates.elems > 1 {
                try %class<m>.push: pack-code($method, 1, :is-method);
                try %class<m>.push: pack-code($_, 2, :is-method) for $method.candidates;
            } else {
                try %class<m>.push: pack-code($method, 0, :is-method);
            }
        }
    }
    for @privates -> $method {
        try {
            if $method.candidates.elems > 1 {
                try %class<m>.push: pack-code($method, 1, '!' ~ $method.name, :is-method);
                try %class<m>.push: pack-code($_, 2, '!' ~ $method.name, :is-method) for $method.candidates;
            } else {
                try %class<m>.push: pack-code($method, 0, '!' ~ $method.name, :is-method);
            }
        }
    }
    for object.^attributes -> $a {
        try %class<a>.push: pack-variable($a.name, $a, :is-attribute);
    }
    @elems.push: %class;
}
