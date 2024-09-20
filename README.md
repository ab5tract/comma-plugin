# Comma
## The Raku IDE plugin for IntellIj IDEA

This is a continuation of the work done by the fine folks at Edument[^discontinuation] to build and support
an IDE for Raku. The standalone mode is off the table for the moment, but running as a plugin
in `IntelliJ IDEA` is fully supported.

It is now built with `Gradle IntelliJ Platform Plugin 2.0` and requires IntelliJ IDEA version `2024.2` or later, 
available for download [here](https://www.jetbrains.com/idea/download/) (scroll down for the free Community edition).

### Install

1) Download `comma-2.0.zip` from the latest [release](https://github.com/ab5tract/comma-plugin/releases)
2) Open `Settings > Plugins` in IntelliJ IDEA and find the gear icon, then select `Install plugin from disk` from the
subsequent menu.
3) Restart the IDE
4) Enjoy!

### Build

To build, simply:

1) Clone this repo, open it in IntelliJ IDEA
2) Select `build > build`[^build] (or `intellij platform > runIde` to run in a sandbox) from the 
Gradle target options (look for the elephant on the left side of the IDEA window).
3) Enjoy! And please report any issues you have to the issue tracker here.

[^discontinuation]: Read the [discontinuation](https://commaide.com/discontinued) announcement.
[^build]: The plugin will be produced as `build/distributions/comma-${version}.zip`.
