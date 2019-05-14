sub foo(:$one-two-three) {
    say ($one-two-three // "default")
}

f<caret>oo(one-two-three => "One");
