package org.raku.comma.pm.impl

import com.intellij.execution.ExecutionException
import com.intellij.openapi.project.Project
import org.raku.comma.pm.RakuPackageManager
import org.raku.comma.pm.RakuPackageManagerKind
import org.raku.comma.pm.ui.RakuPMWidget
import org.raku.comma.utils.RakuCommandLine
import org.raku.comma.utils.ZefCommandLine

class RakuZefPM(location: String, private val project: Project) : RakuPackageManager(location) {
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
    override fun install(): Int {
        val cmd = ZefCommandLine(project)
        cmd.addParameter("install")
        installList.forEach { install ->
            cmd.addParameter(install)
        }
        return RakuPMWidget.initAndRun(project, cmd)
    }

    @Throws(ExecutionException::class)
    override fun getInstalledDistributions(project: Project): Set<String?> {
        val cmd = RakuCommandLine(project)
        cmd.addParameter(location)
        cmd.addParameter("list")
        cmd.addParameter("--installed")
        return HashSet(cmd.executeAndRead(null))
    }
}
