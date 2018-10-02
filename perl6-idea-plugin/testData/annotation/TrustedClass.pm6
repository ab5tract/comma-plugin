class A {
    trusts B;
    method !mmm1 {};
    method !mmm2 {}; };

class B {
    method test {
        A!A::mmm1;
    }
}
