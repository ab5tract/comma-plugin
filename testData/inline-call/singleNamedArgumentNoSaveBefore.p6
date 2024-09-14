sub f<caret>oo(:$one-two-three) {
    say ($one-two-three // "default")
}

foo(one-two-three => "One");
