class Foo {
    method aaa {
        my $m = self.aaa + 3;
        class B {
            method bbb {
                say $<caret>m;
            }
        }
        B.bbb;
    }
}