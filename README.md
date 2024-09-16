# Comma
## The Raku IDE plugin for IntellIj IDEA

_**NOTE:** There is an issue when opening existing Comma projects that were created in earlier versions. Please use `New project from Existing Sources...` rather than `Open` and make sure to select `Yes` when it prompts you about overwriting an existing `.idea` file in the project directory._

This is a continuation of the work done by the fine folks at Edument[^discontinuation] to build and support
an IDE for Raku. The standalone mode is off the table for the moment, but running as a plugin
in `IntelliJ IDEA` is fully supported.

It is now built with `Gradle IntelliJ Platform Plugin 2.0`.

To build, simply:

1) Clone this repo, open it in IntelliJ IDEA (version `2024.2` or later),
2) Select `build > build`[^build] (or `intellij platform > runIde` to run in a sandbox) from the Gradle target options (look for the elephant on the left side of the IDEA window).
3) Enjoy! And please report any issues you have to the issue tracker here.

[^discontinuation]: Read the [discontinuation](https://commaide.com/discontinued) announcement.
[^build]: The plugin will be produced as `build/distributions/comma-${version}.zip`.
