package org.raku.comma.project

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.util.Disposer
import org.raku.comma.services.RakuBackupSDKService

class CommaProjectActivity : ProjectActivity {

    private fun isCommaProject(project: Project): Boolean {

        // TODO: This is apparently the right approach, but
        // val projectIndex : ProjectFileIndex = ProjectRootManager.getInstance(project).fileIndex
        // val file = com.intellij.openapi.project.ProjectUtil.guess
        val file = project.guessProjectDir();


        if (file?.isDirectory!!) {
            val path = file.toNioPath()
            if (path.resolve("META6.json").toFile().exists()) {
                return true
            }
            if (path.resolve("META.info").toFile().exists()) {
                return true
            }
        }
        return false
    }

    override suspend fun execute(project: Project) {
        if (! isCommaProject(project)) return

        // TODO: Try to get the damned "secondary" SDK saved at startup
//        val service = project.getService(RakuBackupSDKService::class.java)
//        Disposer.register(service, Disposable {})

    }
}