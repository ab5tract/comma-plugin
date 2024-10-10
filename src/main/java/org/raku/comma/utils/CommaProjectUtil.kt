package org.raku.comma.utils

import com.intellij.execution.ExecutionException
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import kotlinx.serialization.json.Json
import org.raku.comma.metadata.data.MetaFile
import org.raku.comma.sdk.RakuSdkUtil
import org.raku.comma.services.project.RakuProjectDetailsService
import org.raku.comma.services.project.RakuProjectSdkService
import java.io.File
import java.nio.file.Path

/**
 * A simpler, Comma-exclusive re-imagination of NewProjectUtil.
 */
object CommaProjectUtil {
    private val LOG = Logger.getInstance(
        CommaProjectUtil::class.java
    )

    @JvmStatic
    fun collectDependenciesOfModules(project: Project, modules: List<String>): List<String> {
        val sdk = project.service<RakuProjectSdkService>().sdkPath
        try {
            val locateScript = RakuUtils.getResourceAsFile("zef/gather-deps.raku")
                ?: throw ExecutionException("Resource bundle is corrupted: locate script is missing")
            val depsCollectorScript = RakuCommandLine(sdk)
            depsCollectorScript.addParameter(locateScript.absolutePath)
            modules.forEach {
                depsCollectorScript.addParameter(it)
            }
            return depsCollectorScript.executeAndRead(locateScript)
        } catch (e: ExecutionException) {
            RakuSdkUtil.reactToSdkIssue(project, "Cannot use current Raku SDK")
            return ArrayList()
        }
    }

//    @JvmStatic
//    fun createNewProject(wizard: CommaNewProjectWizard) {
//        val title = "Initialization..."
//        val warmUp = Runnable { ProjectManager.getInstance().defaultProject } // warm-up components
//        val proceed = ProgressManager.getInstance().runProcessWithProgressSynchronously(warmUp, title, true, null)
//        if (proceed && wizard.showAndGet()) {
//            createFromWizard(wizard)
//        }
//    }
//
//    @JvmStatic
//    fun createFromWizard(wizard: CommaAbstractProjectWizard) {
//        try {
//            doCreate(wizard)
//        } catch (e: IOException) {
//            UIUtil.invokeLaterIfNeeded {
//                Messages.showErrorDialog(
//                    e.message,
//                    "Project Initialization Failed"
//                )
//            }
//        }
//    }
//
//    @Throws(IOException::class)
//    private fun doCreate(wizard: CommaAbstractProjectWizard) {
//        val projectFilePath = wizard.newProjectFilePath
//        val file = Paths.get(projectFilePath)
//        for (p in getOpenProjects()) {
//            if (isSameProject(file, p)) {
//                focusProjectWindow(p, false)
//                return
//            }
//        }
//
//        val projectBuilder = wizard.projectBuilder
//        LOG.debug("builder $projectBuilder")
//
//        val projectManager = ProjectManagerEx.getInstanceEx()
//        try {
//            val projectDir: Path?
//            if (wizard.storageScheme == StorageScheme.DEFAULT) {
//                projectDir = file.parent
//                if (projectDir == null) {
//                    throw IOException("Cannot create project in '$projectFilePath': no parent file exists")
//                }
//            } else {
//                projectDir = file
//            }
//            if (projectDir == null) return
//
//            Files.createDirectories(projectDir)
//
//            val newProject: Project?
//            if (projectBuilder == null || !projectBuilder.isUpdate) {
//                val name = wizard.projectName
//                newProject = if (projectBuilder == null) {
//                    projectManager.newProject(
//                        file, OpenProjectTask.build()
//                            .asNewProject()
//                            .withProjectName(name)
//                    )
//                } else {
//                    projectBuilder.createProject(name, projectFilePath)
//                }
//            } else {
//                newProject = null
//            }
//
//            if (newProject == null) {
//                return
//            }
//
//            val sdkHome = wizard.newProjectSdk
//            if (sdkHome != null) {
//                CommandProcessor.getInstance().executeCommand(
//                    newProject,
//                    {
//                        ApplicationManager.getApplication()
//                            .runWriteAction {
//                                applyRakuSdkToProject(
//                                    newProject,
//                                    sdkHome
//                                )
//                            }
//                    }, null, null
//                )
//            }
//
//            if (projectBuilder != null) {
//                // validate can require project on disk
//                if (!ApplicationManager.getApplication().isUnitTestMode) {
//                    newProject.save()
//                }
//
//                if (!projectBuilder.validate(null, newProject)) {
//                    return
//                }
//
//                projectBuilder.commit(newProject, null, ModulesProvider.EMPTY_MODULES_PROVIDER)
//            }
//
//            if (!ApplicationManager.getApplication().isUnitTestMode) {
//                val needToOpenProjectStructure = projectBuilder == null || projectBuilder.isOpenProjectSettingsAfter
//                StartupManager.getInstance(newProject).runAfterOpened {
//                    // ensure the dialog is shown after all startup activities are done
//                    ApplicationManager.getApplication().invokeLater(
//                        {
//                            if (needToOpenProjectStructure) {
//                                // FIXME re-instantiate Comma modules configurator showing
//                                // ModulesConfigurator.showDialog(newProject, null, null);
//                            }
//                            ApplicationManager.getApplication()
//                                .invokeLater(
//                                    {
//                                        val toolWindow = ToolWindowManager.getInstance(
//                                            newProject
//                                        )
//                                            .getToolWindow(
//                                                ToolWindowId.PROJECT_VIEW
//                                            )
//                                        toolWindow?.activate(
//                                            null
//                                        )
//                                    },
//                                    ModalityState.NON_MODAL,
//                                    newProject.disposed
//                                )
//                        },
//                        ModalityState.NON_MODAL,
//                        newProject.disposed
//                    )
//                }
//            }
//
//            updateLastProjectLocation(file)
//            val options =
//                OpenProjectTask.build().withProject(newProject).withProjectName(file.fileName.toString())
//            ProjectManagerEx.getInstanceEx().openProject(projectDir, options)
//
//            if (!ApplicationManager.getApplication().isUnitTestMode) {
//                SaveAndSyncHandler.getInstance().scheduleProjectSave(newProject)
//            }
//        } finally {
//            projectBuilder?.cleanup()
//        }
//    }

    @JvmStatic
    fun applyRakuSdkToProject(project: Project, rakuSdk: String) {
        val service = project.service<RakuProjectSdkService>()
        service.setProjectSdkPath(rakuSdk)
    }

    @JvmStatic
    fun isRakudoCoreProject(project: Project): Boolean {
        return project.service<RakuProjectDetailsService>().isProjectRakudoCore()
    }

    // Technically this should also be available at the Facet level so that different project-modules can have their
    // own META6.json. Fix this later!
    @JvmStatic
    fun projectDependencies(project: Project): List<String> {
        return if (projectHasMetaFile(project)) metaFile(project).depends else listOf()
    }

    @JvmStatic
    fun projectDependenciesDeep(project: Project): List<String> {
        return if (projectHasMetaFile(project))
                    collectDependenciesOfModules(project, metaFile(project).depends)
              else listOf()
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    fun metaFile(project: Project): MetaFile {
        val meta6path = "%s%sMETA6.json".format(project.basePath, File.separator)
        check(Path.of(meta6path).toFile().exists()) { "There is no META6.json file in project path " + project.basePath }
        return json.decodeFromString(File(meta6path).readText())
    }

    fun projectHasMetaFile(project: Project): Boolean {
        val meta6path = "%s%sMETA6.json".format(project.basePath, File.separator)
        return Path.of(meta6path).toFile().exists()
    }

}
