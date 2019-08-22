### How to write new tests

* On adding a new test file, make sure it overrides `getProjectDescriptor` method and
  returns a `Perl6LightProjectDescriptor`, otherwise a Null Pointer Exception will be
  thrown during setUp of test object without a stacktrace.
