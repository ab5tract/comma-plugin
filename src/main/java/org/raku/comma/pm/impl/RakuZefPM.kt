package org.raku.comma.pm.impl

import com.intellij.execution.ExecutionException
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import org.raku.comma.pm.RakuPackageManager
import org.raku.comma.pm.RakuPackageManagerKind
import org.raku.comma.pm.ui.RakuPMWidget
import org.raku.comma.utils.zef.CommandLineTarget
import org.raku.comma.utils.zef.ZefCommandLine
import java.nio.file.Path
import java.time.Instant.now
import java.util.concurrent.CompletableFuture

class RakuZefPM(private val project: Project, commandLocation: String? = null) : RakuPackageManager(commandLocation) {
    override val kind: RakuPackageManagerKind = RakuPackageManagerKind.ZEF

    private val installList: MutableList<String> = mutableListOf()
    override fun addInstall(install: String) {
        installList.add(install)
    }

    override fun installEach() {
        val originalList = setOf(*installList.toTypedArray())
        installList.clear()

        originalList.forEach { install ->
            installList.add(install)
            install()
            installList.clear()
        }
    }

    @Throws(ExecutionException::class)
    override fun installPackageManager(completion: CompletableFuture<Int>): Int {
        val tmpDir = FileUtil.createTempDirectory("zef-git-", now().toString(), true)

        val gitClone = ZefCommandLine(project, CommandLineTarget.GIT).withWorkingDirectory(Path.of(tmpDir.canonicalPath)) as ZefCommandLine
        gitClone.addParameters("clone", "https://github.com/ugexe/zef.git")
        val cloneStatus = RakuPMWidget.initAndRun(project, gitClone, null)
        if (cloneStatus != 0) {
            completion.complete(cloneStatus)
            return cloneStatus
        }

        if (! tmpDir.resolve("zef").isDirectory) return -1
        val gitPull = ZefCommandLine(project, CommandLineTarget.RAKU).withWorkingDirectory(tmpDir.resolve("zef").toPath()) as ZefCommandLine
        gitPull.addParameters("-I.", "bin/zef", "install", ".")
        val installStatus = RakuPMWidget.initAndRun(project, gitPull, null)
        if (installStatus != 0) {
            completion.complete(installStatus)
            return installStatus
        }

        val jsonInstallStatus = installJson()
        completion.complete(jsonInstallStatus)

        return jsonInstallStatus
    }

    override fun installJson(): Int {
        val jsonInstall = ZefCommandLine(project, CommandLineTarget.ZEF)
        jsonInstall.addParameters("install", "JSON::Fast")
        return RakuPMWidget.initAndRun(project, jsonInstall, null)
    }

    @Throws(ExecutionException::class)
    override fun install(): Int {
        val cmd = ZefCommandLine(project)
        cmd.addParameter("install")
        installList.forEach { install ->
            cmd.addParameter(install)
        }
        return RakuPMWidget.initAndRun(project, cmd, null)
    }
}
