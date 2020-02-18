class AAA {
    method check-or-no($a, $b) {
        if $a { $b } else { 42 }
    }

    method check {
        if False { 15 } else { 42 };
    }
}
