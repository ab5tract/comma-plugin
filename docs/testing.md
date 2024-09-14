### How to write new tests

* On adding a new test file, make sure it overrides `getProjectDescriptor` method and
  returns a `RakuLightProjectDescriptor`, otherwise a Null Pointer Exception will be
  thrown during setUp of test object without a stacktrace.
