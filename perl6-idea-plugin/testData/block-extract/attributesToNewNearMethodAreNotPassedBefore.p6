class Foo {
    has $!a;
    has $.b;

    method one {
        <selection>say $!a;
        say $!b;
        say $.b;
        say $!unknown-attribute</selection>;
    }
}