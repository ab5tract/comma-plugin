package org.raku.comma.pm

import com.intellij.execution.ExecutionException
import com.intellij.openapi.project.Project

abstract class RakuPackageManager(var location: String) {
    abstract val kind: RakuPackageManagerKind

    @Throws(ExecutionException::class)
    abstract fun install(): Int
    abstract fun installEach()
    abstract fun addInstall(install: String)

    @Throws(ExecutionException::class)
    abstract fun getInstalledDistributions(project: Project): Set<String?>?
}
