class AAA {
    method !mm {}

    method m {
        while True {
            self!mm;
        }
    }
}

class BBB {
    method mm {
        AAA.m<caret>;
    }
}