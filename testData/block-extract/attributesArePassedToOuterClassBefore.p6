class Outer {
    class Inner {
        has $!foo;

        method extract-from() {
            <selection>say $!foo</selection>;
        }
    }
}