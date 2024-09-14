# Comma
## The Raku IDE plugin for IntellIj IDEA

This is a continuation of the work done by the fine folks at Edument[^discontinuation] to build and support
an IDE for Raku. The standalone mode is off the table for the moment, but running as a plugin
in `IntelliJ IDEA` is fully supported.

It is now built with `Gradle IntelliJ Platform Plugin 2.0`.

To build, simply:
1) Clone this repo, open it in IntelliJ IDEA (version `2024.02` or later),
2) Adjust `build.gradle.kts` such that `jetbrainsRuntime` reflects your OS and CPU architecture.
3) Select `buildPlugin` or `runIde` from the Gradle target options.
4) Enjoy! And please report any issues you have to the issue tracker here.

[^discontinuation]: Read the [discontinuation](https://commaide.com/discontinued) announcement.
