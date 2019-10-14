grammar MAIN {
    token TOP {
        <.sequence-element>*
        <.bogus-end>?
    }

    token bogus-end {
        <.start-token('BAD_CHARACTER')>
        .+
        <.end-token('BAD_CHARACTER')>
    }

    token sequence-element {
        || <.sequence-element-literal-text>
        || <.sequence-element-literal-open-tag>
        || <.sequence-element-literal-close-tag>
        || <.sigil-tag>
    }

    token sequence-element-literal-text {
        <.start-token('LITERAL_TEXT')>
        <-[<]>+
        <.end-token('LITERAL_TEXT')>
    }

    token sequence-element-literal-open-tag {
        <?before '<' <![/]> <!sigil>>
        <.start-element('LITERAL_OPEN_TAG')>
        <.lt>
        [
            <.literal-tag-name>
            <.ws>
            [<.literal-tag-attribute> <.ws> || <.sigil-tag> <.ws>]*
            <.gt>?
        ]?
        <.end-element('LITERAL_OPEN_TAG')>
    }

    token sequence-element-literal-close-tag {
        <?before '</' <!sigil>>
        <.start-element('LITERAL_CLOSE_TAG')>
        <.lt>
        <.close>
        [
            <.ws>
            <.literal-tag-name>
            <.gt>?
        ]?
        <.end-element('LITERAL_CLOSE_TAG')>
    }

    token literal-tag-name {
        <.start-token('LITERAL_TAG_NAME')>
        <.ident>
        <.end-token('LITERAL_TAG_NAME')>
    }

    token literal-tag-attribute {
        <.start-element('LITERAL_TAG_ATTRIBUTE')>
        <.start-token('ATTRIBUTE_NAME')>
        <.identifier>
        <.end-token('ATTRIBUTE_NAME')>
        <.ws>
        [
            <.start-token('ATTRIBUTE_EQUALS')>
            '='
            <.end-token('ATTRIBUTE_EQUALS')>
            <.ws>
            <.literal-tag-attribute-value>?
        ]?
        <.end-element('LITERAL_TAG_ATTRIBUTE')>
    }

    token literal-tag-attribute-value {
        || <.start-token('ATTRIBUTE_QUOTE')>
           '"'
           <.end-token('ATTRIBUTE_QUOTE')>
           <.start-token('ATTRIBUTE_VALUE')>
           <-["]>*
           <.end-token('ATTRIBUTE_VALUE')>
           [
                <.start-token('ATTRIBUTE_QUOTE')>
                '"'
                <.end-token('ATTRIBUTE_QUOTE')>
           ]?
        || <.start-token('ATTRIBUTE_QUOTE')>
           \'
           <.end-token('ATTRIBUTE_QUOTE')>
           <.start-token('ATTRIBUTE_VALUE')>
           <-[']>*
           <.end-token('ATTRIBUTE_VALUE')>
           [
                <.start-token('ATTRIBUTE_QUOTE')>
                \'
                <.end-token('ATTRIBUTE_QUOTE')>
           ]?
       || <.start-token('ATTRIBUTE_VALUE')>
           \S*
           <.end-token('ATTRIBUTE_VALUE')>
    }

    token sigil {
        # Single characters we can always take as a tag sigil
        || <[.$@&:|]>
        # The ? and ! for boolification must be followed by a . or $ tag sigil or
        # { expression. <!DOCTYPE>, <?xml>, and <!--comment--> style things
        # must be considered literal.
        || <[?!]> <[.$>{]>
    }

    token sigil-tag {
        || <.sigil-tag-use>
        || <.sigil-tag-apply>
    }

    token sigil-tag-use {
        <?before '<:use'>
        <.start-element('USE')>
        <.tlt>
        <.decl-sigil>
        <.start-token('DECL_OPENER')>
        'use'
        <.end-token('DECL_OPENER')>
        [
            <.hws>
            [ <.single-quote-string> <.hws>? <.tgt>? ]?
        ]?
        <.end-element('USE')>
    }

    token sigil-tag-apply {
        <?before '<|'>
        <.start-element('APPLY')>
        <.tlt>
        <.apply-sigil>
        [
            <.start-token('MACRO_NAME')>
            <.identifier>
            <.end-token('MACRO_NAME')>
            <.hws>?
            <.arglist>?
            <.hws>?
            [
                <.tgt>
                <.sequence-element>*
                [
                    <?before '</|'>
                    <.tlt>
                    <.tclose>
                    <.apply-sigil>
                    <.tgt>?
                ]?
            ]?
        ]?
        <.end-element('APPLY')>
    }

    token arglist {
        <.start-element('ARGLIST')>
        <.start-token('OPEN_PAREN')>
        '('
        <.end-token('OPEN_PAREN')>
        <.ws>
        [
            [
                <.expression>
                <.ws>
                [
                    <.start-token('COMMA')>
                    ','
                    <.end-token('COMMA')>
                    <.ws>
                ]?
            ]*
            <.ws>
            [
                <.start-token('CLOSE_PAREN')>
                ')'
                <.end-token('CLOSE_PAREN')>
                <.hws>?
            ]?
        ]?
        <.end-element('ARGLIST')>
    }
    
    token expression {
        :my $*LEFTSIGIL = '';
        :my $*PREC = '';
        :my $*SUB_PREC = '';
        :my $*ASSOC = '';
        :my $*NEXT_TERM = '';
        :my $*FAKE = 0;

        <.opp-start-expr>

        # Currently no prefixes and postfixes; just keep the OPP API
        # happy.
        <.opp-start-prefixes> <.opp-end-prefixes>
        <.term>
        <.opp-start-postfixes> <.opp-end-postfixes>

        [
            <?before <.ws> <.infixish> <.ws>>
            <.ws>
            <.opp-start-infix>
            { $*NEXT_TERM = '' }
            <.infixish>
            <.opp-end-infix>
            [<!before <.ws> <[\]})]>> <.ws>]?

            <.opp-start-prefixes> <.opp-end-prefixes>
            <.nextterm>?
            <.opp-start-postfixes> <.opp-end-postfixes>
        ]*
        <.opp-end-expr>

        # Zero-width marker token to get nesting correct.
        [
        || <!before $>
           <.start-token('END_OF_EXPR')>
           <?>
           <.end-token('END_OF_EXPR')>
        || <?>
        ]
    }

    token nextterm {
#        || <?{ $*NEXT_TERM eq 'dotty' }>
#           <.start-element('METHOD_CALL')>
#           <.start-token('DOTTY_NEXT_TERM')> <?> <.end-token('DOTTY_NEXT_TERM')>
#           <.dottyop>
#           <.end-element('METHOD_CALL')>
        || <.term>
    }

    token infixish {
        <.start-element('INFIX')>
        <.infix>
        <.end-element('INFIX')>
    }

    token infix {
        [
        || <.start-token('INFIX')>
           [
           || '!===' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           || '===' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           || 'and' { $*PREC = 'd=' } { $*ASSOC = 'left' }
           || '==' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           || '!=' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           || '<=' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           || '>=' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           || 'eq' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           || 'ne' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           || 'le' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           || 'ge' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           || 'lt' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           || 'gt' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           || '&&' { $*PREC = 'l=' } { $*ASSOC = 'left' }
           || '||' { $*PREC = 'k=' } { $*ASSOC = 'left' }
           || 'or' { $*PREC = 'c=' } { $*ASSOC = 'left' }
           || '*' { $*PREC = 'u=' } { $*ASSOC = 'left' }
           || '/' { $*PREC = 'u=' } { $*ASSOC = 'left' }
           || '%' { $*PREC = 'u=' } { $*ASSOC = 'left' }
           || '+' { $*PREC = 't=' } { $*ASSOC = 'left' }
           || '-' { $*PREC = 't=' } { $*ASSOC = 'left' }
           || 'x' { $*PREC = 's=' } { $*ASSOC = 'left' }
           || '~' { $*PREC = 'r=' } { $*ASSOC = 'left' }
           || '<' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           || '>' { $*PREC = 'm=' } { $*ASSOC = 'left' }
           ]
           <.end-token('INFIX')>
        ]
    }

    token term {
        || <.single-quote-string>
        || <.int>
        || <.rat>
        || <.num>
    }

    token single-quote-string {
        <.start-element('STRING_LITERAL')>
        <.start-token('STRING_QUOTE_SINGLE')>
        \'
        <.end-token('STRING_QUOTE_SINGLE')>
        [
            <.start-token('STRING_TEXT')>
            <-[']>+
            <.end-token('STRING_TEXT')>
        ]?
        [
            <.start-token('STRING_QUOTE_SINGLE')>
            \'
            <.end-token('STRING_QUOTE_SINGLE')>
        ]? 
        <.end-element('STRING_LITERAL')>
    }
    
    token int {
        <.start-element('INT_LITERAL')>
        <.start-token('INT_LITERAL')>
        '-'? \d+
        <.end-token('INT_LITERAL')>
        <.end-element('INT_LITERAL')>
    }

    token rat {
        <.start-element('RAT_LITERAL')>
        <.start-token('RAT_LITERAL')>
        '-'? \d* '.' \d+
        <.end-token('RAT_LITERAL')>
        <.end-element('RAT_LITERAL')>
    }

    token num {
        <.start-element('NUM_LITERAL')>
        <.start-token('NUM_LITERAL')>
        '-'? \d* '.' \d+ <[eE]> '-'? \d+
        <.end-token('NUM_LITERAL')>
        <.end-element('NUM_LITERAL')>
    }

    token ws {
        [
        || <.start-token('SYNTAX_WHITE_SPACE')>
           \s+
           <.end-token('SYNTAX_WHITE_SPACE')>
        || <?>
        ]
    }

    token hws {
        <.start-token('SYNTAX_WHITE_SPACE')>
        \h+
        <.end-token('SYNTAX_WHITE_SPACE')>
    }

    token tlt {
        <.start-token('TEMPLATE_TAG_OPEN')>
        '<'
        <.end-token('TEMPLATE_TAG_OPEN')>
    }

    token tgt {
        <.start-token('TEMPLATE_TAG_CLOSE')>
        '>'
        <.end-token('TEMPLATE_TAG_CLOSE')>
    }

    token tclose {
        <.start-token('TEMPLATE_TAG_SLASH')>
        '/'
        <.end-token('TEMPLATE_TAG_SLASH')>
    }

    token lt {
        <.start-token('LITERAL_TAG_OPEN')>
        '<'
        <.end-token('LITERAL_TAG_OPEN')>
    }

    token gt {
        <.start-token('LITERAL_TAG_CLOSE')>
        '>'
        <.end-token('LITERAL_TAG_CLOSE')>
    }

    token close {
        <.start-token('LITERAL_TAG_SLASH')>
        '/'
        <.end-token('LITERAL_TAG_SLASH')>
    }

    token decl-sigil {
        <.start-token('TEMPLATE_TAG_DECL_SIGIL')>
        ':'
        <.end-token('TEMPLATE_TAG_DECL_SIGIL')>
    }

    token apply-sigil {
        <.start-token('TEMPLATE_TAG_APPLY_SIGIL')>
        '|'
        <.end-token('TEMPLATE_TAG_APPLY_SIGIL')>
    }

    token identifier {
        <.ident> [ <[-']> <.ident> ]*
    }

    token ident {
        [<.alpha> || '_'] \w*
    }
}
