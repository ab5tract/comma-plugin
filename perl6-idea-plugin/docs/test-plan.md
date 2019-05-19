## Test plan for Comma release

#### Test plan

- [x] Build Comma Complete with `ant complete-build`
- [x] Installer works. It creates a shortcut and can run Comma after installation process is finished
- [x] Create new project of type Application, "Test::Test" with entry point "test"
- [ ] Project name should be suggested as "test"
- [x] A tip is shown
- [x] The project has `META6.json`, `bin` and `lib`(marked as sources), `t`(marked as tests)

- [x] `Help->About` has correct image and data

- [ ] Startup Comma
- [ ] Install plugin into IDEA instance

#### Comma-specific

- [ ] Try to import new project
- [ ] Try to import from github
- [x] Try to break build, check if exception feedback works
- [x] Open project settings. 
  - [x] Look if SDK is set
  - [x] Look at Modules tab, change `Name`, `License`, check if error appear when fields are empty
  - [x] Apply, close, open again, check SDK and updated values of module
  - [x] Check buttons to add/remove modules are absent
  - [x] Close, open `META6.json`, check updated values

#### Common

- [x] Create new project of type application
- [x] Script in `bin/` has highlighting
- [x] Module file has highlighting
- [x] Check completion of subs from Core
- [x] Type a couple of if-else statements, check it is formatted on e.g. brace closing
- [x] Parens are auto-closed


* Modules
- [x] Create new module under `lib/Test`, `First`. Name field has `Test::` prefix. Check `META6.json` for `provides` section update.
- [x] Delete `lib/Test/First.pm6`. Check `META6.json` is updated.
- [x] Move `Test::Test` to `t`. Check `META6.json` is updated.
- [x] Create new module in `t/` named `Foo::Bar`. Check if `META6.json`is the same.

* Tests
- [x] Create new test, `one` in `t/`. It has highlighting and is named `one.t`
- [x] Create new test, `two.t6` in `t/`. It has highlighting and is named `one.t6`
- [x] Create new test, `three.t` in `t/`. It has highlighting and is named `three.t`
- [x] Create new test in `bin/`, `foo`. It is created and has `foo.t` as its name.
- [x] Create new test in `lib/Test`, `foo`. It is created and has `foo.t` as its name.
- [x] `META6.json` is not affected by tests.

* Scripts
- [x] Create new script in `bin/`, `a`. It is created, has a default template and named as `a.p6`.
- [x] Create new script in `bin/`, `b.p6`. It is created, has a default template and named as `b.p6`.
- [x] Create new script in `bin/`, `c.pl6`. It is created, has a default template and named as `c.pl6`.
- [x] Create new script in `lib/Test`, `a`. It is created, has a default template and named as `a.p6`.
- [x] Create new script in `lib/Test`, `a`. It is created, has a default template and named as `a.p6`.
- [x] Create new script in `t/`, `a`. It is created, has a default template and named as `a.p6`.

* Create a new project of type Module, `Test::Module`.

- [x] Project name is suggested to be mangled script name.

* Annotations

- [x] Unknown variable
- [x] Unknown private method `self!p(1, [1,2], :foo<bar>)` -> quickfix creates a new private method with signature created.
- [x] Unknown module `Foo::Bar` and `Foo::Bario`. Quickfix of the first one adds `Foo::Bar` to dependencies, quickfix of the second one stubs a local module.

- [x] TypeName is completed
- [x] Import external modules, e.g. `NativeCall` and `Cro::HTTP::Middleware`; Check if we can complete both `NativeCall::Native` and `Cro::HTTP::Middleware::Request` types
- [x] Check we complete methods in class that does `Middleware::Request` and in class that does `NativeCall::Native`
- [x] Check if we can create both test run configuration and usual run configuration.
  - [x] Run some basic tests, with nesting, see output
  - [x] Run usual run configuration
  - [x] Set a breakpoint on some `say` line, run debug, see if it pauses and shows stack trace
- [x] Fold a block, fold a block of blocks
- [x] Open Structure View, check if displayed: regex, class, inner class, check element nesting and icons.
- [x] Try out commenter, one line, selected multiple lines
- [x] Try if we can use project modules
- [x] Try to use Move Statement Up/Down
- [x] Check outer plugin installation
