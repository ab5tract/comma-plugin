package org.raku.comma.pm

import com.intellij.execution.ExecutionException
import java.util.concurrent.CompletableFuture

abstract class RakuPackageManager(val location: String?) {
    abstract val kind: RakuPackageManagerKind

    @Throws(ExecutionException::class)
    abstract fun installPackageManager(completion: CompletableFuture<Int>): Int
    abstract fun installJson(): Int

    @Throws(ExecutionException::class)
    abstract fun install(): Int
    abstract fun installEach()
    abstract fun addInstall(install: String)
}
