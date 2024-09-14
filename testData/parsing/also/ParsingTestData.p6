class A { }
role B { }
class C {
    also is A;
    also does B does Positional;
}