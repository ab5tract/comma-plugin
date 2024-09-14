role Test {
    has @!two;

    method one() {
        say @!two;
    }
}

class TestClass does Test {
    method two() {
        say @!two;
    }
}