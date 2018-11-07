### Perl6MetaDataComponent

#### Description

This document describes a way to unify access to Perl 6 module meta information.

A class `Perl6MetaDataComponent` is created and registered as IDEA module component.

On module component initialization, it tries to find either `META6.json` and `META.info`
files in module root directory.

In case of its absence, a balloon message is created which notifies user about it
and offers user to add it. If user agrees, a dummy `META6.json` file is created
and is opened in IDEA editor. Otherwise it does not do anything.

In case of incorrect JSON, a balloon error message is showed to a user with a button
to open the file.

If there is a need to read or write a module metadata, this component is accessed
by IDEA means and its methods are used.

#### API

A `Perl6MetaDataComponent` has such methods:

* `boolean isMetadataFile(String path)` - indicates if given stringified path could be a Perl 6 metadata file.
* `boolean isMetaDataExist()` - indicates if metadata file exists or was created on start.
* `boolean isModuleInDepends(String)` - indicates if given module name is provided by
`depends` section of metadata.
* `boolean isModuleInTestDepends(String)` - indicates if given module name is provided by
`test-depends` section of metadata.
* `boolean isModuleInBuildDepends(String)` - indicates if given module name is provided by
`build-depends` section of metadata.
* `void createStubMetaFile(boolean)` - creates a dummy metadata file at module root and opens
it in an editor if its first parameter is true.
* `void addNamespaceToProvides(String name)` - adds module name e.g. `Foo::Bar` into `provides` section
of metadata.
* `void removeNamespaceFromProvides(String name)` - removes module name e.g. `Foo::Bar` from `provides` section
of metadata.
* 

#### Accessors 

* `String getName()` and `void getName(String)`
* `String getDescription()` and `void getDescription(String)`
* `String getVersion()` and `void getVersion(String)`
* `String getAuth()` and `void getAuth(String)`
* `String getLicense()` and `void getLicense(String)`
