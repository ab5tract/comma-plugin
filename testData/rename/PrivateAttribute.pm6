role Test {
    has @!one;

    method one() {
        say @!one;
    }
}

class TestClass does Test {
    method two() {
        say @!one;
    }
}