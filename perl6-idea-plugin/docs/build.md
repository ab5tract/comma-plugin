### Comma build process

Do inside some work-related directory, e.g. `comma`.
* `git clone https://github.com/edument/intellij-community.git`
* `cd intellij-community; git checkout comma`
* Do steps from `intellij-idea` readme, noticeably creating `IDEA jdk` JDK and setting it to project, running of `getPlugins.sh` script.
* `git clone https://github.com/edumentab/perl6-idea-plugin.git comma-build` into the intellij-community folder.
* Start IDEA, open project from `intellij-community` directory.
* `Unregistered VCS root detected` for `comma-bulid` - let IDEA add this root.
* At this point, `json`, `tap4j`, and `miglayout` dependencies may be missing.
  * Open `Project Structure` -> `Libraries` and add them manually using `From Maven`.
  * Maven coordinates are: `org.tap4j:tap4j:4.3`, `org.json:json:20171018`, and `com.miglayout:miglayout-swing:5.1`.

#### How to build a plugin

Starting from `intellij-community` directory.
* `cd comma-build/perl6-idea-plugin`
* `./gradlew buildPlugin`
* Plugin will be in `build/distributions` directory

#### How to build a standalone Comma

Starting from `intellij-community` directory.
* `cd comma-build`
* `ant`
* The build artifacts are placed inside of `intellij-community/out/comma-se/artifacts`.

#### How to run Comma from IDEA Run Configuration for testing

**WARNING**: some features that are present in built artifact are not present in this configuration, notably error reporter used is always Intellij's one, not Edument-based.

* Open Run Configurations tab. Create a new one:
  - Type: Application.
  - Name: `Comma`(does not matter for build).
  - Main class: `com.intellij.idea.Main` (type `Main` in Search by Name and select needed)
  - VM options: -ea  -Xmx192m -Didea.is.internal=true -Didea.platform.prefix=CommaCore -Didea.paths.selector=Comma
  - Working directory: `intellij-community/bin`
  - Classpath of module: `edument.comma.supporter`
  - JRE: `IDEA jdk` (default for the project)

* Run `Comma` configuration.

#### How to run Perl 6 Plugin from IDEA Run Configuration for testing

* Open Run Configurations. Create a new one(you may copy `IDEA with Python plugin` and do appropriate changes if wanted):

  - Type: Application.
  - Name: `IDEA with Perl 6 plugin`(does not matter for build).
  - Main class: com.intellij.idea.Main (type `Main` in Search by Name and select needed)
  - VM options: `-ea -Xmx192m -Didea.is.internal=true -Didea.config.path=../config -Didea.system.path=../system`
  - Working directory: `intellij-community/bin`
  - Classpath of module: `perl6.community.plugin.main`.
  - JRE: `IDEA jdk`

* In `Before launch` section add new task, `Run Gradle task`.
  * In modal window select directory `comma-build/perl6-idea-plugin`
  * As for tasks, write `preparePluginMETA` in.
  * Move it so it is executed before all other tasks(namely `Build`) - see notes below.

* Run `IDEA with Perl 6 Plugin` configuration.

#### How to run tests

* `cd comma-build/perl6-idea-plugin`
* `./gradlew test`

### Important notes about build process

###### `plugin.xml` status

The build is done using `ant` for Comma standalone jar and using `gradle` for plugin.
It causes an inconvenience, because `gradle` uses plugin metadata inside of `perl6-idea-plugin/resources/META-INFO/plugin.xml` to build the plugin itself, perform tests, this metadata is also needed for plugins' run configuration to work. On the other hand, `ant` build treats presence of this metadata as failure.

Current workaround for this is to store a plugin metadata inside of `perl6-idea-plugin/resources/META-INFO/plugin-meta` directory and copy it in needed place as a part of gradle tasks(for testing, building or running).

From the other side, `ant` removes this metatada as a part of build process to be able to build a standalone release when needed. This file should not(it will not break anything though) be commited and it is added into `.gitignore` file.

###### Commons between plugin and IDE

Currently, metadata is distributed between `ide` directory and `perl6-idea-plugin`.

In `perl6-idea-plugin/resources/META-INF` we have:
* `plugin.xml` - contains plugin metadata, placed inside of `plugin-meta` directory(see section above).
* `CommaCorePlugin.xml` - contains core plugin metadata.
* `common-meta.xml` - contains common classes for both plugin and core plugin.

In `ide/src/META-INF` we have:
* `comma-common.xml` - contains all common classes for both plugin and core plugin.
* `comma-core.xml` - contains core plugin metadata.
* `CommaCorePlugin.xml` - includes both `comma-common.xml` and `comma-core.xml`.

* Currently it seems to be impossible to simplify schema of IDE related metadata.
* Insides of `common-meta.xml` and `comma-common.xml` are the same and must be the same.
The same goes for content of `CommaCorePlugin.xml` of plugin and `comma-core.xml` - content related to classes must be the same.
It may be a future goal to reduce this duplication, thought for now each build tool restricts such file schema changes.
