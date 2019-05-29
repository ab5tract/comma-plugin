class AAA {
    method check-or-no($a, $b) {
        if $a { $b } else { 42 }
    }

    method check {
        self.check-<caret>or-no(False, 15);
    }
}