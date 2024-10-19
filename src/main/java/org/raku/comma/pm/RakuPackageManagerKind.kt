package org.raku.comma.pm

enum class RakuPackageManagerKind {
    EMPTY, ZEF;

    override fun toString(): String {
        if (this == ZEF) return "zef"
        return "..."
    }
}
