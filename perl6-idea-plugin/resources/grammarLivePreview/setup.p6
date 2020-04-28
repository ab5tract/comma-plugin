# Tracing / output logic

my $cur-node;
my $error;
for __GRAMMAR_LIVE_PREVIEW_GRAMMAR_NAME__.^methods.grep({ try Regex.ACCEPTS($_) }) {
    my $name = .name;
    .wrap: -> |args {
        my $prev-node = $cur-node;
        $cur-node = { n => $name, s => args[0].pos, c => [] };
        my \result = callsame;
        LEAVE {
            if result {
                $cur-node<p> = True;
                $cur-node<e> = result.pos;
            }
            else {
                $cur-node<p> = False;
            }
            if $prev-node {
                $prev-node<c>.push($cur-node);
                $cur-node = $prev-node;
            }
        }
        result
    }
}

END {
    my %json;
    %json<t> = $_ with $cur-node;
    %json<e> = .message with $error;
    say "\n___PARSER__OUTPUT__BEGINS__";
    say to-json %json;
}

# ========== JSON CODE FROM JSON::Fast ==========

use nqp;

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
