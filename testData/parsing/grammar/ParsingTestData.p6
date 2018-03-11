grammar Foo {
    token TOP {
        ^^
        [
        || (a & a)* % y
        || bc | d
        ]+ % z
        $$
    }

    method panic($message) {
        sub foo () {

        }
        die $message;
    }

    proto token foo { * }

    token blah {
        a: b:? c:!
        d ** 1..5
        e ** 1^..4
        f ** 5..*
        g ** {$a .. $b - 1}
    }

    token bs-and-cb {
        \w \\ \s \g
        \x42AB \o111 \c123 \c[BUTTERFLY]
        { say "goodness, code in regex" }
        [omg]*
    }

    token quotes {
        'single'+
        "double"+
        'single $vars not interpolated'?
        "double $vars interpolated"+
    }

    token cclass {
        <[a..df..hxyz]+foo-:L>
    }

    token mod {
        :i :!i :1i :dba("omg")
    }

    rule foo {
        a b :!s c d :s e f
    }

    token vars {
        ($x) $0
        $*x
        $<a>=[abc]
        @a=n+
    }
}