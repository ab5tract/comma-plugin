role Role {
    method !foo {}
}

class Test does Role {
    method test1 {
        self!foo;
        self!foo;
    }

    method test2 {
        self!foo;
        self!f<caret>oo;
    }
}