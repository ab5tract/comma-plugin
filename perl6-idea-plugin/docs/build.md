### Comma build process

#### Preparation

Do inside a work-related directory, for example, `comma`.
* `git clone https://github.com/edument/intellij-community.git`
* `cd intellij-community`
* `git checkout comma`
* Do steps from `intellij-community` repo [README](https://github.com/JetBrains/intellij-community/#opening-the-intellij-source-code-for-build), creating `IDEA jdk` JDK and setting it to the project, running `getPlugins.sh` script.
* `git clone https://github.com/edumentab/perl6-idea-plugin.git comma-build` into the intellij-community folder.
* Start IDEA instance, open existing project from `intellij-community` directory.
* A `Unregistered VCS root detected` for `comma-bulid` will appear - let IDEA add this root.
* At this point, `json`, `tap4j`, and `miglayout` dependencies may be missing.
  * Open `Project Structure` -> `Libraries` and add them manually using `From Maven` submenu.
  * Maven coordinates are: `org.tap4j:tap4j:4.3`, `org.json:json:20171018`, and `com.miglayout:miglayout-swing:5.1`. Add them to the module `edument.perl6.plugin`.

#### How to build a plugin

While in `perl6-idea-plugin` directory:
* `./gradlew --rerun-tasks -b community.gradle buildPlugin` or `./gradlew --rerun-tasks -b complete.gradle buildPlugin` for Community version or Complete version.
* Plugin will be placed in `build/distributions` directory.

#### How to build standalone Comma

While in `comma-build` directory.
* `ant community-build` or `ant complete-build` for Community version or Complete version.
* Built images will be in `../out/commaCT/artifacts` directory.

#### How to run Comma from IDEA Run Configuration for testing features during development

**WARNING**: some features that are present in built artifact are not present in this configuration, such as:

* Error reporter used is always Intellij's one, not Edument-based. 
* Because of plugin absence, `Check out from version control` always has no options, but those are present in final build.

* Open Run Configurations tab. Create a new one:
  - Type: Application.
  - Name: `Comma`(does not matter for build).
  - Main class: `com.intellij.idea.Main` (type `Main` in Search by Name and select needed)
  - VM options: `-ea  -Xmx192m -Didea.is.internal=true -Didea.platform.prefix=CommaCore -Didea.paths.selector=Comma`
  - Working directory: `intellij-community/bin`
  - Classpath of module: `edument.perl6.plugin`
  - JRE: `IDEA jdk` (default for the project)

* Run `Comma` configuration.

#### How to run Perl 6 Plugin from IDEA Run Configuration for testing

* Open Run Configurations. Create a new one(you may copy `IDEA with Python plugin` and do appropriate changes if wanted):

  - Type: Application.
  - Name: `IDEA with Perl 6 plugin`(does not matter for build).
  - Main class: com.intellij.idea.Main (type `Main` in Search by Name and select needed)
  - VM options: `-ea -Xmx192m -Didea.is.internal=true`
  - Working directory: `intellij-community/bin`
  - Classpath of module: `edument.perl6.plugin`
  - JRE: `IDEA jdk`

Add a Gradle task in "Before launch" section:

  - Project: `perl6-idea-plugin` directory.
  - Tasks: `preparePluginsMeta`
  - Arguments:
    - If you want **Community** edition: `--rerun-tasks -b community.gradle`
    - If you want **Complete** edition: `--rerun-tasks -b complete.gradle`

* Run `IDEA with Perl 6 Plugin` configuration.

#### How to run tests

In `comma-build/perl6-idea-plugin`:
* `./gradlew test`

### How to update version

-  Version of the plugin is set in `gradle.properties`
- Version and build date in Comma "About" splash screen is set in `CommaCoreApplicationInfo.xml`
- **Both those cases are handled by `set-version` script**, please do use it as `20??.??.?`.
