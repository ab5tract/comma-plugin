class AAA {
    method check<caret>-or-no($a, $b) {
        if $a { $b } else { 42 }
    }

    method check {
        self.check-or-no(False, 15);
    }
}