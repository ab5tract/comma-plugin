package org.raku.comma.project

import com.intellij.ide.impl.OpenProjectTask
import com.intellij.ide.impl.ProjectUtilCore
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.projectImport.ProjectOpenProcessor
import com.intellij.util.containers.stream
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.Nls
import java.io.File
import java.nio.file.Paths
import java.util.*

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

        removeOldCommaProjectFiles(projectDirectory)

        val isValidIdeaProject = ProjectUtilCore.isValidProjectPath(nioPath)
        val options = OpenProjectTask(
            true, projectToClose, !isValidIdeaProject, isValidIdeaProject
        )
        val project = ProjectManagerEx.getInstanceEx().openProject(nioPath, options)
        if (project != null) {

            var iniialized = false
            while (!iniialized) {
                iniialized = project.isInitialized
            }

            runBlocking {
                importProjectAfterwardsAsync(project, virtualFile)
            }
        }
        return project
    }

    override suspend fun importProjectAfterwardsAsync(project: Project, file: VirtualFile) {
        val projectBuilder = RakuProjectBuilder(null)
        projectBuilder.fileToImport = file.toString()
        projectBuilder.commit(project, null, null)
        project.save()
    }

    override fun canImportProjectAfterwards(): Boolean {
        return true
    }

    // XXX: This feels like quite a messy hack... but it works.
    private fun removeOldCommaProjectFiles(basePath: VirtualFile) {
        val oldImls = basePath.toNioPath().toFile().listFiles().stream()
                            .filter { file -> file.extension == "iml" }.toList()

        if (oldImls.isEmpty() || oldImls.size > 1) return

        val imlFile = oldImls.first();
        val scanner: Scanner
        try {
            scanner = Scanner(imlFile)
        } catch (ignored: Exception) {
            return
        }

        var isOldComma = false
        while (scanner.hasNextLine()) {
            val lineFromFile = scanner.nextLine()
            if (lineFromFile.contains("PERL6_MODULE_TYPE")) {
                isOldComma = true
                break
            }
        }

        if (isOldComma) {
            Notifications.Bus.notify(
                Notification(
                    "raku.messages",
                    "Old-style Comma project found. Converting.",
                    NotificationType.INFORMATION
                )
            )

            FileUtil.delete(imlFile)

            val oldIdeaPath = Paths.get(basePath.toNioPath().toString(), ".idea").toString()
            val oldIdeaDirectory = File(oldIdeaPath)
            if (oldIdeaDirectory.exists() && oldIdeaDirectory.isDirectory) {
                FileUtil.delete(oldIdeaDirectory)
            }
        }
    }

    override val isStrongProjectInfoHolder: Boolean
        get() = true
}
