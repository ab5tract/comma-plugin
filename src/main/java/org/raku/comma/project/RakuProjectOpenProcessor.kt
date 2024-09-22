package org.raku.comma.project

import com.intellij.ide.impl.OpenProjectTask
import com.intellij.ide.impl.ProjectUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.projectImport.ProjectOpenProcessor
import com.intellij.util.containers.stream
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.Nls
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList

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

        removeOldCommaProjectFiles(nioPath)

        val isValidIdeaProject = ProjectUtilCore.isValidProjectPath(nioPath)
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
        val projectBuilder = RakuProjectBuilder(null)
        projectBuilder.fileToImport = file.toString()
        projectBuilder.commit(project, null, null)
    }

    override fun canImportProjectAfterwards(): Boolean {
        return true
    }

    // XXX: This feels like quite a messy hack... but it works.
    //      The goal is to nuke any PERL6* .iml file, as well as the .idea directory
    private fun removeOldCommaProjectFiles(basePath: Path) {
        val oldImls = basePath.toFile().listFiles().stream()
                              .filter { file -> file.extension == "iml" }
                              .toList()

        if (oldImls.isEmpty()) return

        val isOldCommaImlFiles: MutableList<File> = ArrayList()
        for (imlFile in oldImls) {
            val scanner: Scanner
            try {
                scanner = Scanner(imlFile)
            } catch (ignored: Exception) {
                return
            }
            while (scanner.hasNextLine()) {
                val lineFromFile = scanner.nextLine()
                if (lineFromFile.contains("PERL6_MODULE_TYPE")) {
                    isOldCommaImlFiles.add(imlFile)
                    break
                }
            }
        }

        if (isOldCommaImlFiles.isEmpty()) return

        for (isOldCommaImlFile in isOldCommaImlFiles) {
            FileUtil.delete(isOldCommaImlFile)
        }

        val oldIdeaPath = Paths.get(basePath.toString(), ".idea").toString()
        val oldIdeaDirectory = File(oldIdeaPath)
        if (oldIdeaDirectory.exists() && oldIdeaDirectory.isDirectory) {
            FileUtil.delete(oldIdeaDirectory)
        }
    }

    override val isStrongProjectInfoHolder: Boolean
        get() = true
}
