package org.raku.comma.project

import com.intellij.ide.impl.OpenProjectTask
import com.intellij.ide.impl.ProjectUtilCore
import com.intellij.openapi.application.EDT
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.projectImport.ProjectOpenProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.Nls

class RakuProjectOpenProcessor : ProjectOpenProcessor() {
    override val name: @Nls String
        get() = "Raku"

    override fun canOpenProject(file: VirtualFile): Boolean {
        if (file.isDirectory) {
            if (file.toNioPath().resolve("META6.json").toFile().exists()) {
                return true
            }
            if (file.toNioPath().resolve("META.info").toFile().exists()) {
                return true
            }
        }
        val fileName = file.name
        return fileName == "META6.json" || fileName == "META.info"
    }

    override fun doOpenProject(
        virtualFile: VirtualFile,
        projectToClose: Project?,
        forceOpenInNewFrame: Boolean
    ): Project? {
        val projectDirectory = if (virtualFile.isDirectory) virtualFile else virtualFile.parent
        val nioPath = projectDirectory.toNioPath()

        val isValidIdeaProject = ProjectUtilCore.isValidProjectPath(projectDirectory.toNioPath())
        val options = OpenProjectTask(
            true, projectToClose, !isValidIdeaProject, isValidIdeaProject
        )
        val project = ProjectManagerEx.getInstanceEx().openProject(nioPath, options)
        if (project != null) {
            runBlocking {
                importProjectAfterwardsAsync(project, virtualFile)
            }
        }
        return project
    }

    override suspend fun importProjectAfterwardsAsync(project: Project, file: VirtualFile) {
        withContext(Dispatchers.EDT) {
            val projectBuilder = RakuProjectBuilder(null)
            projectBuilder.fileToImport = file.toString()
            projectBuilder.commit(project)
        }
    }

    override fun canImportProjectAfterwards(): Boolean {
        return true
    }

    override val isStrongProjectInfoHolder: Boolean
        get() = true
}
