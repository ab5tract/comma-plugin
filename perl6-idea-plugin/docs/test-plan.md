## Test plan for Comma release

##### How to use this test plan

Currently, the testing environments are GNU/Linux, Windows and OSX.

Considering we are having 2 editions and 3 operating systems to test with,
it makes us 6 test runs in total.

As we have automatic tests for large chunks of functionality, this manual test
plan focuses on not repeatedly testing components that were tested automatically,
but on covering areas where we lack proper automated testing: OS integration,
UI components, components that are hard to test like `META6.json` changes listener.

However, to ensure that components tested were loaded in the first place,
this suite contains test cases of sort "Check an unknown variable annotator to work".

Tests that involve file creation _must_ be checked on all operating systems due to changes
in path handling etc.

Tests that check work of internal components such as code structure handler,
can be checked on a single platform only and using a single edition
(it must always be a Complete one, as we care about Complete edition first and foremost).

##### Integration part

Here we test ability of our product to be built and installed by the end-user.

- [ ] Build Comma Complete with `ant complete-build`
- [ ] Build Comma Complete plugin with `ant complete-plugin-build`
- [ ] Windows-specific
  - Installer works. It creates a shortcut and can run Comma after installation process is finished
  - Installer, installation paths contain Edument info
  - It creates file associations for: `.p6`, `.pm6`, `pl6`, `.pod6`
  - It can be uninstalled cleanly
- [ ] Install the plugin into IntelliJ IDEA 2020.3

##### Plugin-specific

- [ ] At `Plugins` modal window, the plugin's version and description are correct

##### Comma-specific

- [ ] A tip is shown on start
- [ ] `Help->About` has correct image and data

##### Common

Here we test ability of our product to create new projects and consume existing ones.

- [ ] Create a new project of type Application, "Test::Test" with entry point "test"
- [ ] Project name should be suggested as "test"
- [ ] The project has `META6.json`, `bin` and `lib`(marked as sources), `t`(marked as tests)
- [ ] Add a dependency `Foo::Bar` into `test-depends` section of `META6.json`, open `Project Structure`
  - [ ] Check if Sdk is change-able
  - [ ] Check if module metadata is shown based on `META6.json`
  - [ ] Edit fields and apply changes, close model, check if `META6.json` was updated
- [ ] Close the project, open it again, no exceptions should be thrown, close the project
- [ ] Remove `.idea` directory from new project directory
- [ ] Try to `Open` the same project based on `META6.json`
- [ ] Check if directories are marked correctly, `lib`(as sources), `t`(as tests)
- [ ] Try to import a project from github, `https://github.com/jnthn/json-path`
- [ ] Try to trigger an exception, check if the exception feedback shows Edument data, cancel

Here we test ability of our product to manipulate files inside a project.

For the last opened project.

- [ ] Script in `bin/` has highlighting
- [ ] Module file has highlighting
- [ ] Check completion of subs from Core
- [ ] Type a couple of if-else statements, check it is formatted on e.g. brace closing

* Modules
- [ ] Create a new module under `lib/Test`, `First`. Name field has `Test::` prefix. Check `META6.json` for `provides` section update
- [ ] Delete `lib/Test/First.pm6`. Check `META6.json` is updated
- [ ] Move `Test::Test` to `t`. Check `META6.json` is updated
- [ ] Create new module in `t/` named `Foo::Bar`. Check if `META6.json`is the same

* Tests

- [ ] Create new test, `one` in `t/`. It has highlighting and is named `one.t`
- [ ] Create new test, `two.t6` in `t/`. It has highlighting and is named `one.t6`
- [ ] Create new test, `three.t` in `t/`. It has highlighting and is named `three.t`
- [ ] Create new test in `bin/`, `foo`. It is created and has `foo.t` as its name
- [ ] Create new test in `lib/Test`, `foo`. It is created and has `foo.t` as its name
- [ ] `META6.json` is not affected by tests

* Scripts

- [ ] Create new script in `bin/`, `a`. It is created, has a default template and named as `a.p6`
- [ ] Create new script in `bin/`, `b.p6`. It is created, has a default template and named as `b.p6`
- [ ] Create new script in `bin/`, `c.pl6`. It is created, has a default template and named as `c.pl6`
- [ ] Create new script in `lib/Test`, `a`. It is created, has a default template and named as `a.p6`
- [ ] Create new script in `lib/Test`, `a`. It is created, has a default template and named as `a.p6`
- [ ] Create new script in `t/`, `a`. It is created, has a default template and named as `a.p6`

* Misc

Here we test various cases of functionality that must be visibly present,
thus ensuring our `plugin.xml` and other metadata files were loaded and processed by
the Idea or that Comma contains all necessary classes.

- [ ] Unknown variable annotation
- [ ] TypeName is completed
- [ ] Import external modules, `NativeCall` and `Cro::HTTP::Middleware`
  - [ ] `NativeCall::Native` type is completed
  - [ ] `Cro::HTTP::Middleware::Request` type is completed
  - [ ] Methods are completed in a class that does `Middleware::Request` role
  - [ ] Private methods are completed in a class that does `NativeCall::Native` role
- [ ] Check if we can create both test run configuration and run configuration.
  - [ ] Run tests, `ok 42, 'One', ok True; subtest { is 3, 3, "Three is three"; is 3, 4, "Three is four" }, "Subtest";`
  - [ ] Run usual run configuration
  - [ ] Set a breakpoint on a `say` line, run debug, see if it pauses and shows stack trace
  - [ ] Run coverage for tests, check presence of coverage data
  - [ ] Run profile on run configuration
- [ ] Open Structure View, check if displayed: regex, class, inner class, check element nesting and icons
