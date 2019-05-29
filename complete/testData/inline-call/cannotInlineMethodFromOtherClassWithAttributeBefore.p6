class AAA {
    has $.foo;

    method m {
        if $!foo { $!foo } else { $!foo };
    }
}

class BBB {
    method mm {
        AAA.m<caret>;
    }
}