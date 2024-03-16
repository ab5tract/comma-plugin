# Comma Source Dump

Following Comma development being [discontinued](https://commaide.com/discontinued),
it was promised that the source code of Comma would be made available to the Raku
community, to give anybody who wishes a shot at carrying it forward as an open source
project. This repository contains that source.

There were a small number of custom patches to the IntelliJ platform source that have
been made available in another repository, [`intellij-ide-fork`](https://github.com/Raku/intellij-ide-fork).
See the [build doc in here](https://github.com/Raku/intellij-ide-plugin/tree/master/perl6-idea-plugin/docs)
for information on how `intellij-ide-fork` is used relative to this repository.

Given the relatively complex build toolchain and cost of keeping up with new versions
of the IDE platform, the most effective way forward for somebody interested in carrying
this forward would be to maintain a plugin only. Probably it's also going to be
easiest to do that by creating an entirely new plugin project, and bringing the code in
piecemeal from this repository. A plugin-only approach has a far simpler build setup than
is found here. See [this tutorial](https://plugins.jetbrains.com/docs/intellij/custom-language-support-tutorial.html)
for some pointers.

Note that this repository is basically a source dump that can serve as a seed for an open source Raku plugin
for IntelliJ platform IDEs. We wish anybody attempting that the best of luck!
