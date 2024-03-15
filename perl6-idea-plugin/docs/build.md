### Comma build process

#### Preparation

Currently, Comma is based upon 223 Intellij revision/branch.
Raku Comma plugin supports versions starting revision 221 (2022.1.1).

To develop Comma, version of IDEA you use **must** correspond to version
of the platform used (see above for the current version).

Do the following steps inside a work-related directory, for example, `comma`.
* `git clone https://github.com/edumentab/intellij-community.git`
* `cd intellij-community`
* `git checkout comma-223.4884`
* Run the `getPlugins.sh` script
* `git clone https://github.com/edumentab/perl6-idea-plugin.git comma-build` (so the structure is `intellij-community/comma-build`)
* Make sure revisions of android-related repos are fresh enough for the IDEA checkout:
  * `cd android; git checkout 223.4884`
  * `cd ..`
* Start IDEA instance, open existing project from `intellij-community` directory. Make sure you are using correct IDEA version.
* An `Unregistered VCS root detected` for `comma-bulid` will appear - let IDEA add this root.
* If some dependency libraries are missing, add them to the project

#### SDK

SDK for the project is `jbr-17`, you can download it from https://github.com/JetBrains/JetBrainsRuntime/releases

#### How to build a plugin

While in the `intellij-community` directory.
* `./comma-build/complete-plugin.cmd -Dintellij.build.use.compiled.classes=false -Dintellij.build.target.os=linux`
* The plugin will be placed in `../out/commaCT/artifacts/CT-plugins/` and `out/commaCP/artifacts/CP-plugins/` directory depending on the version built (CT or CP).

#### How to build a standalone Comma

While in the `intellij-community` directory.
* `./comma-build/complete-build.cmd -Dintellij.build.use.compiled.classes=false -Dintellij.build.target.os=linux`
* Built images will be in `../out/comma/artifacts` directory.

#### How to run Comma from IDEA Run Configuration for testing features during development

**WARNING**: some features that are present in built artifact are not present in this configuration, such as:

* Error reporter used is always Intellij's one, not Edument-based.

* Open Run Configurations tab. Create a new Run Configuration of type "Application":
  - Name: `Comma` (does not matter for the build, you also should specify an edition, "Complete" or "Community")
  - SDK: `Bundled`
  - Main class: `com.intellij.idea.Main` (type `Main` in Search by Name and select needed)
  - VM options: `-ea  -Xmx192m -Didea.is.internal=true -Didea.platform.prefix=CommaCore -Didea.paths.selector=Comma`
  - The working directory: `intellij-community/bin`
  - Classpath of module: `edument.perl6.comma.complete` or `edument.perl6.comma.community` depending on the edition you want to run
  - Shorten command line: `@argfiles`

* Run the configuration.

### How to update version

- Version and build date in Comma "About" splash screen is set in `CommaCoreApplicationInfo.xml` respectively for both editions.
- **Both those cases are handled by `set-version` script**, please do use it as `20??.??.?`.
