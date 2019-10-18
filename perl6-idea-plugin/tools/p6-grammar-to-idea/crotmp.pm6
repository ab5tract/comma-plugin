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
        || <.sequence-element-literal-close-tag>
        || <.sequence-element-literal-open-tag>
        || <.sequence-element-literal-text>
        || <.sigil-tag>
    }

    token sequence-element-group {
        <.start-element('TAG_SEQUENCE')>
        <.sequence-element>*
        <.end-element('TAG_SEQUENCE')>
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
           [
                || <.start-token('ATTRIBUTE_VALUE')>
                   <-["<]>+
                   <.end-token('ATTRIBUTE_VALUE')>
                || <.sigil-tag>
                || <.start-token('ATTRIBUTE_VALUE')>
                   '<'
                   <.end-token('ATTRIBUTE_VALUE')>
           ]*
           [
                <.start-token('ATTRIBUTE_QUOTE')>
                '"'
                <.end-token('ATTRIBUTE_QUOTE')>
           ]?
        || <.start-token('ATTRIBUTE_QUOTE')>
           \'
           <.end-token('ATTRIBUTE_QUOTE')>
           [
                || <.start-token('ATTRIBUTE_VALUE')>
                   <-['<]>+
                   <.end-token('ATTRIBUTE_VALUE')>
                || <.sigil-tag>
                || <.start-token('ATTRIBUTE_VALUE')>
                   '<'
                   <.end-token('ATTRIBUTE_VALUE')>
           ]*
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
        || <.sigil-tag-topic>
        || <.sigil-tag-variable>
        || <.sigil-tag-iteration>
        || <.sigil-tag-condition>
        || <.sigil-tag-call>
        || <.sigil-tag-sub>
        || <.sigil-tag-macro>
        || <.sigil-tag-body>
        || <.sigil-tag-apply>
        || <.sigil-tag-use>
    }

    token sigil-tag-topic {
        <?before '<.'>
        <.start-element('TOPIC_ACCESS')>
        <.tlt>
        <.dot>
        [
            <.deref>
            <.tgt>?
        ]?
        <.end-element('TOPIC_ACCESS')>
    }

    token sigil-tag-variable {
        <?before '<$'>
        <.start-element('VARIABLE_ACCESS')>
        <.tlt>
        <.start-token('VARIABLE_NAME')>
        '$' <.identifier>?
        <.end-token('VARIABLE_NAME')>
        [
            [ <.dot> <.deref>? ]?
            <.tgt>
        ]?
        <.end-element('VARIABLE_ACCESS')>
    }

    token sigil-tag-iteration {
        <?before '<@'>
        <.start-element('ITERATION')>
        <.tlt>
        <.iter-sigil>
        [
            <.deref>
            [
                <.tgt>
                <.sequence-element-group>
                [
                    <?before '</@'>
                    <.tlt>
                    <.tclose>
                    <.iter-sigil>
                    <.tgt>?
                ]?
            ]?
        ]?
        <.end-element('ITERATION')>
    }

    token sigil-tag-condition {
        <?before '<' <[?!]>>
        <.start-element('CONDITION')>
        <.tlt>
        <.cond-sigil>
        [
            [
            || <.dot> <.deref>?
            || <.block>
            ]
            [
                <.tgt>
                <.sequence-element-group>
                [
                    <?before '</' <[?!]>>
                    <.tlt>
                    <.tclose>
                    <.cond-sigil>
                    <.tgt>?
                ]?
            ]?
        ]?
        <.end-element('CONDITION')>
    }

    token block {
        <.start-token('OPEN_CURLY')>
        '{'
        <.end-token('OPEN_CURLY')>
        <.ws>
        [
            <.expression>
            <.ws>
            [
                <.start-token('CLOSE_CURLY')>
                '}'
                <.end-token('CLOSE_CURLY')>
            ]?
        ]?
    }

    token sigil-tag-call {
        <?before '<&'>
        <.start-element('CALL')>
        <.tlt>
        <.call-sigil>
        [
            <.start-token('SUB_NAME')>
            <.identifier>
            <.end-token('SUB_NAME')>
            <.hws>?
            <.arglist>?
            <.hws>?
            <.tgt>?
        ]?
        <.end-element('CALL')>
    }

    token sigil-tag-sub {
        <?before '<:sub'>
        <.start-element('SUB')>
        <.tlt>
        <.decl-sigil>
        <.start-token('DECL_OPENER')>
        'sub'
        <.end-token('DECL_OPENER')>
        [
            <.hws>
            [
                <.start-token('SUB_NAME')>
                <.identifier>
                <.end-token('SUB_NAME')>
                <.hws>?
                <.signature>?
                <.hws>?
                [
                    <.tgt>
                    <.sequence-element-group>
                    [
                        <?before '</:'>
                        <.tlt>
                        <.tclose>
                        <.decl-sigil>
                        [
                            <.start-token('DECL_OPENER')>
                            'sub'
                            <.end-token('DECL_OPENER')>
                        ]?
                        <.tgt>?
                    ]?
                ]?
            ]?
        ]?
        <.end-element('SUB')>
    }

    token sigil-tag-macro {
        <?before '<:macro'>
        <.start-element('MACRO')>
        <.tlt>
        <.decl-sigil>
        <.start-token('DECL_OPENER')>
        'macro'
        <.end-token('DECL_OPENER')>
        [
            <.hws>
            [
                <.start-token('MACRO_NAME')>
                <.identifier>
                <.end-token('MACRO_NAME')>
                <.hws>?
                <.signature>?
                <.hws>?
                [
                    <.tgt>
                    <.sequence-element-group>
                    [
                        <?before '</:'>
                        <.tlt>
                        <.tclose>
                        <.decl-sigil>
                        [
                            <.start-token('DECL_OPENER')>
                            'macro'
                            <.end-token('DECL_OPENER')>
                        ]?
                        <.tgt>?
                    ]?
                ]?
            ]?
        ]?
        <.end-element('MACRO')>
    }

    token sigil-tag-body {
        <?before '<:body'>
        <.start-element('BODY')>
        <.tlt>
        <.decl-sigil>
        <.start-token('DECL_OPENER')>
        'body'
        <.end-token('DECL_OPENER')>
        [
            <.hws>?
            <.tgt>
        ]?
        <.end-element('BODY')>
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
                <.sequence-element-group>
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

    token signature {
        <.start-element('SIGNATURE')>
        <.start-token('OPEN_PAREN')>
        '('
        <.end-token('OPEN_PAREN')>
        <.ws>
        [
            [
                <.parameter>
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
        <.end-element('SIGNATURE')>
    }

    token parameter {
        <.start-element('PARAMETER')>
        <.start-token('VARIABLE_NAME')>
        '$' <.identifier>?
        <.end-token('VARIABLE_NAME')>
        <.end-element('PARAMETER')>
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
        || <.variable>
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

    token variable {
        <.start-element('VARIABLE_ACCESS')>
        <.start-token('VARIABLE_NAME')>
        '$' <.identifier>?
        <.end-token('VARIABLE_NAME')>
        [ <.dot> <.deref>? ]?
        <.end-element('VARIABLE_ACCESS')>
    }

    token deref {
        <.deref-item>
        [<.dot> <.deref-item>?]*
    }

    token deref-item {
        || <.deref-item-smart>
    }
    
#    token deref-item:sym<method> {
#        <identifier> '(' \s* ')'
#    }

    token deref-item-smart {
        <.start-element('DEREF_SMART')>
        <.start-token('IDENTIFER')>
        <.identifier>
        <.end-token('IDENTIFER')>
        <.end-element('DEREF_SMART')>
    }

#    token deref-item:sym<hash-literal> {
#        '<' <( <-[>]>* )> '>'
#    }
#    token deref-item:sym<array> {
#        '[' <index=.expression> ']'
#    }
#    token deref-item:sym<hash> {
#        '{' <key=.expression> '}'
#    }

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

    token dot {
        <.start-token('DOT')>
        '.'
        <.end-token('DOT')>
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

    token iter-sigil {
        <.start-token('TEMPLATE_TAG_ITER_SIGIL')>
        '@'
        <.end-token('TEMPLATE_TAG_ITER_SIGIL')>
    }

    token cond-sigil {
        <.start-token('TEMPLATE_TAG_COND_SIGIL')>
        <[?!]>
        <.end-token('TEMPLATE_TAG_COND_SIGIL')>
    }

    token call-sigil {
        <.start-token('TEMPLATE_TAG_CALL_SIGIL')>
        '&'
        <.end-token('TEMPLATE_TAG_CALL_SIGIL')>
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
