class Foo {
    .say;

    #| Fight mechanics
    method duel(Magician $a, Magician $b) {
        self.duel<caret>;
    }
    #= Magicians only, no mortals.
}