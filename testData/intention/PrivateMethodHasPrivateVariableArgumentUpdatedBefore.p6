class A {
  has $.a;

  method a {
    self!f<caret>oo($!a);
  }
}