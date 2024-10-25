package org.raku.comma.services;

import java.util.List;

public class RakuServiceConstants {
    public static final String PROJECT_SETTINGS_FILE = "comma.xml";
    public static final String RAKU_EXTERNAL_MODULES_FILE = "raku-external-modules.xml";
    public static final String RAKU_DEPENDENCY_DETAILS_FILE = "raku-dependency-details.xml";

    public static final String APP_RAKU_SDK_OPTIONS = "comma-raku-sdks.xml";
    public static final String APP_RAKU_DISTRO = "comma-raku-distro.xml";

    public static final List<String> PREINSTALLED_MODULES = List.of(
        "CompUnit::Repository::Staging",
        "CompUnit::Repository::FileSystem",
        "CompUnit::Repository::Installation",
        "CompUnit::Repository::AbsolutePath",
        "CompUnit::Repository::Unknown",
        "CompUnit::Repository::NQP",
        "CompUnit::Repository::Raku",
        "CompUnit::Repository::RepositoryRegistry",
        "NativeCall",
        "NativeCall::Types",
        "NativeCall::Compiler::GNU",
        "NativeCall::Compiler::MSVC",
        "Test",
        "Pod::To::Text",
        "Telemetry"
    );

    public static final List<String> PRAGMAS = List.of(
        "v6.c",
        "v6.d",
        "v6.e",
        "MONKEY-GUTS",
        "MONKEY-SEE-NO-EVAL",
        "MONKEY-TYPING",
        "MONKEY",
        "experimental",
        "fatal",
        "internals",
        "invocant",
        "isms",
        "lib",
        "nqp",
        "newline",
        "parameters",
        "precompilation",
        "soft",
        "strict",
        "trace",
        "v6",
        "variables",
        "worries"
    );
}
