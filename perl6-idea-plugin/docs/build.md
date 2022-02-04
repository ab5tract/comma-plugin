### Comma build process

#### Preparation

Currently, Comma is based upon 203 Intellij revision/branch.
Raku Comma plugin supports versions starting revision 203 (2020.03).

To develop Comma, version of IDEA you use **must** correspond to version
of the platform used (see above for the current version).

Do the following steps inside a work-related directory, for example, `comma`.
* `git clone https://github.com/edumentab/intellij-community.git`
* `cd intellij-community`
* `git checkout comma-213.5744`
* Start with steps from `intellij-community` repo [README](https://github.com/JetBrains/intellij-community/#opening-the-intellij-source-code-for-build): creating `IDEA jdk` JDK and setting it to the project, running `getPlugins.sh` script.
* `git clone https://github.com/edumentab/perl6-idea-plugin.git comma-build` (so the structure is `intellij-community/comma-build`)
* Make sure revisions of android-related repos are fresh enough for the IDEA checkout:
  * `cd android; git checkout idea/212.3116.43`
  * `cd ..`
* Start IDEA instance, open existing project from `intellij-community` directory. Make sure you are using correct IDEA version.
* An `Unregistered VCS root detected` for `comma-bulid` will appear - let IDEA add this root.
* At this point, `json` and`tap4j` dependencies may be missing.
  * Open `Project Structure` -> `Libraries` and add them manually using `From Maven` submenu.
  * Maven coordinates are: `org.tap4j:tap4j:4.3`, `org.json:json:20171018`. Add them to the `edument.perl6.plugin` module.

#### SDK

While OpenJDK installations *may* work, it is not assumed and for recent years of development (2020)
we encountered various errors.

If you have Oracle JDK, e.g. 1.8.0_191 or above, it will work fine.

Otherwise, you can use Amazon-based JDK named Corretto, see repo's readme for instructions.

Then set `JAVA_HOME` to point to either oracle JDK or the downloaded one.

#### How to build a plugin

While in the `comma-build` directory.
* `ant community-plugin-build` or `ant complete-plugin-build` for the Community version or Complete version.
* The plugin will be placed in `../out/commaCT/artifacts/CT-plugins/` and `out/commaCP/artifacts/CP-plugins/` directory depending on the version built (CT or CP).

#### How to build a standalone Comma

While in the `comma-build` directory.
* `ant community-build` or `ant complete-build` for the Community version or Complete version.
* Built images will be in `../out/comma/artifacts` directory.

#### How to run Comma from IDEA Run Configuration for testing features during development

**WARNING**: some features that are present in built artifact are not present in this configuration, such as:

* Error reporter used is always Intellij's one, not Edument-based.
* Because of plugin absence, `Check out from version control` always has no options, but those are present in final build.

* Open Run Configurations tab. Create a new Run Configuration of type "Application":
  - Name: `Comma`(does not matter for build, you also should specify an edition, "Complete" or "Community")
  - Main class: `com.intellij.idea.Main` (type `Main` in Search by Name and select needed)
  - VM options: `-ea  -Xmx192m -Didea.is.internal=true -Didea.platform.prefix=CommaCore -Didea.paths.selector=Comma`
  - The working directory: `intellij-community/bin`
  - Classpath of module: `edument.perl6.comma.complete` or `edument.perl6.comma.community` depending on the edition you want to run
  - JRE: `IDEA jdk` (default for the project)

* Run the configuration.

#### How to run Raku Plugin from IDEA Run Configuration for testing

* Open Run Configurations. Create a new Run Configuration (you may copy `IDEA with Python plugin` and do appropriate changes if wanted):
  - Name: `IDEA with Raku plugin`(does not matter for build, you also likely want to specify the edition).
  - Main class: com.intellij.idea.Main (type `Main` in Search by Name and select needed)
  - VM options: `-ea -Xmx192m -Didea.is.internal=true`
  - The working directory: `intellij-community/bin`
  - Classpath of module: `edument.perl6.comma.complete` or `edument.perl6.comma.community` depending on the edition you want to run
  - JRE: `IDEA jdk`

* Run the configuration.

#### How to run tests

While in the `comma-build` directory.
* `ant test`

Or one can execute either `ant test-complete` and `ant test-community` to run dedicated set of tests.
`ant test` target runs `test-complete` and `test-community` sequentially.
The `test-complete` target consists of tests that are common for both editions and dedicated ones. The same is true for `test-community`.

### How to update version

- Version and build date in Comma "About" splash screen is set in `CommaCoreApplicationInfo.xml` respectively for both editions.
- **Both those cases are handled by `set-version` script**, please do use it as `20??.??.?`.
