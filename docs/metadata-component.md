### RakuMetaDataComponent

#### Description

This document describes a way to unify access to Raku module meta information.

A module component `RakuMetaDataComponent` is created and registered as IDEA module component.

During this module component initialization, it tries to find either `META6.json` and `META.info`
files using relative module source directories.

In case of meta file absence, a balloon message is created which notifies user about it
and offers user to add it. If user agrees, a dummy `META6.json` file is created
and is opened in IDEA editor. Otherwise, it does not do anything.

In case of incorrect JSON, a balloon error message is showed to a user with a button
to open meta file.

If there is a need to read or write a module metadata, this component is accessed
by IDEA means and its methods are used.

#### API

`RakuMetaDataComponent` has such methods:

* `void triggerMetaBuild(VirtualFile)` - asks component to populate its data with JSON content of VirtualFile passed.
* `boolean isMetaDataExist()` - indicates if metadata file exists or was created on start.
* `void createStubMetaFile(VirtualFile, boolean)` - creates a dummy metadata file with root specified by first argument and opens
it in an editor if its first parameter is true. If root passed is `null`, it tries to calculate it based on module source roots, if
it fails, asks user with file chooser. If nothing is selected, operation is cancelled. Second argument indicates whether
newly created file will be opened in an editor after creation or not.

#### Accessors

* `String getName()` and `void getName(String)`
* `String getDescription()` and `void getDescription(String)`
* `String getVersion()` and `void getVersion(String)`
* `String getAuth()` and `void getAuth(String)`
* `String getLicense()` and `void getLicense(String)`
* `List<String> getDepends(boolean)` and `void addDepends(String)` - names returned might be normalized (adverbs removed) or not based on getter argument.
* `List<String> getTestDepends(boolean)` and `void addTestDepends(String)` - names returned might be normalized (adverbs removed) or not based on getter argument.
* `List<String> getBuildDepends(boolean)` and `void addBuildDepends(String)` - names returned might be normalized (adverbs removed) or not based on getter argument.
* `Collection<String> getProvidedNames` and `addNamespaceToProvides(String)` + `removeNamespaceFromProvides(String)`
