sub foo(:$one-two-three) {
    say ($one-two-three // "default")
}

say ("One" // "default");
