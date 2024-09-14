class A {
  method private {}
  method public {
    self.private;
    self.private(42);
  }
}