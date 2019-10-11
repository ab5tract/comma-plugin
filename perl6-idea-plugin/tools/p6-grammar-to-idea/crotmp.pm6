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
        || <.sigil-tag>
    }

    token sequence-element-literal-text {
        <.start-token('LITERAL_TEXT')>
        <-[<]>+
        <.end-token('LITERAL_TEXT')>
    }

    token sigil-tag {
        || <.sigil-tag-use>
    }

    token sigil-tag-use {
        <.start-element('USE')>
        <.start-token('USE_OPENER')>
        '<:use'
        <.end-token('USE_OPENER')>
        [
            <.hws>
            # <name=.single-quote-string> \h* '>'
        ]?
        <.end-element('USE')>
    }

    token hws {
        <.start-token('WHITE_SPACE')>
        \h+
        <.end-token('WHITE_SPACE')>
    }
}
