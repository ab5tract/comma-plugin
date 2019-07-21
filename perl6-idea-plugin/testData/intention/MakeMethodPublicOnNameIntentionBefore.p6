class A {
  method !pri<caret>vate {}
  method public {
    self!private;
    self!private(42);
  }
}