### Comma build process (work in progress)

* `git clone https://github.com/JetBrains/intellij-community.git`
* `cd intellij-community`
* Do steps from `intellij-idea` readme, noticeably creating `IDEA jdk` JDK and setting it to project, running of `getPlugins.sh` script (optional).
* `git clone https://github.com/edumentab/perl6-idea-plugin.git` into the intellij-community folder.
* Start IDEA, open the `perl6-idea-plugin` project from inside the `intellij-community` directory.
* `Unregistered VCS root detected` for `perl6-idea-plugin` - let IDEA add this root.
* Open `File` -> `Project Structure` -> `Modules`. `Import module`, select `comma.community.iml`, `next`, `next`, `Apply`.
* Import modules `perl6.community.plugin.impl.iml` and `perl6.community.plugin.main.iml` the same way.
* At this point, `json`, `tap4j`, and `miglayout` dependencies are missing.
  * Open `Project Structure` -> `Libraries` and add them manually using `From Maven`.
  * Maven coordinates are: `org.tap4j:tap4j:4.3`, `org.json:json:20171018`, and `com.miglayout:miglayout-swing:5.1`.
  * Make sure to select the `plugin.main` module from the list of modules to add the library to.

#### How to run Comma Product

* Open Run Configurations. Create a new one:
  - Type: Application.
  - Name: `Comma`.
  - Main class: com.intellij.idea.Main (type `Main` in Search by Name and select needed)
  - VM options: -ea  -Xmx192m -Didea.is.internal=true -Didea.platform.prefix=CommaCore -Didea.paths.selector=Comma
  - Working directory: `intellij-community/bin`
  - Classpath of module: `comma.community`
  - JRE: `IDEA jdk`

* Run `Comma` configuration.
* Enjoy.

#### How to run Perl 6 Plugin

* Open Run Configurations. Create a new one:

  - Type: Application.
  - Name: `IDEA with Perl 6 plugin`.
  - Main class: com.intellij.idea.Main (type `Main` in Search by Name and select needed)
  - VM options: `-ea -Xmx192m -Didea.is.internal=true -Didea.config.path=../config -Didea.system.path=../system`
  - Working directory: `intellij-community/bin`
  - Classpath of module: `perl6.community.plugin.main`.
  - JRE: `IDEA jdk`

* Run `IDEA with Perl 6 Plugin` configuration.
* Enjoy.
