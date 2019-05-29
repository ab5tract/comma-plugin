class AAA {
    method with-self {
        if True { self } else { 42 }
    }
}

class BBB {
    method without {
        AAA.with-s<caret>elf;
    }
}