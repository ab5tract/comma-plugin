token sigil-tag:sym<
token sigil-tag:sym<iteration> {
    :my $*lone-start-line = False;
    '<@'
    [ <?after [^ | $ | \n] \h* '<@'> { $*lone-start-line = True } ]?
    [
    | <deref>
    ]
    [ \h* '>' || <.panic('malformed iteration tag')> ]
    [ <?{ $*lone-start-line }> [ \h* \n | { $*lone-start-line = False } ] ]?

    <sequence-element>*

    :my $*lone-end-line = False;
    '</@'
    [ <?after \n \h* '</@'> { $*lone-end-line = True } ]?
    <close-ident=.ident>?
    [ \h* '>' || <.panic('malformed iteration closing tag')> ]
    [ <?{ $*lone-end-line }> [ \h* \n | { $*lone-end-line = False } ] ]?
}
