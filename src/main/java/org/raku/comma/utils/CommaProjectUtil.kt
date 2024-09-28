package org.raku.comma.utils

import com.intellij.ide.SaveAndSyncHandler
import com.intellij.ide.impl.OpenProjectTask
import com.intellij.ide.impl.ProjectUtil.focusProjectWindow
import com.intellij.ide.impl.ProjectUtil.getOpenProjects
import com.intellij.ide.impl.ProjectUtil.isSameProject
import com.intellij.ide.impl.ProjectUtil.updateLastProjectLocation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.components.StorageScheme
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowId
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.util.ui.UIUtil
import kotlinx.serialization.json.Json
import org.raku.comma.metadata.data.MetaFile
import org.raku.comma.project.projectWizard.CommaAbstractProjectWizard
import org.raku.comma.project.projectWizard.CommaNewProjectWizard
import org.raku.comma.services.RakuProjectDetailsService
import org.raku.comma.services.RakuSDKService
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * A simpler, Comma-exclusive re-imagination of NewProjectUtil.
 */
object CommaProjectUtil {
    private val LOG = Logger.getInstance(
        CommaProjectUtil::class.java
    )

    @JvmStatic
    fun createNewProject(wizard: CommaNewProjectWizard) {
        val title = "Initialization..."
        val warmUp = Runnable { ProjectManager.getInstance().defaultProject } // warm-up components
        val proceed = ProgressManager.getInstance().runProcessWithProgressSynchronously(warmUp, title, true, null)
        if (proceed && wizard.showAndGet()) {
            createFromWizard(wizard)
        }
    }

    @JvmStatic
    fun createFromWizard(wizard: CommaAbstractProjectWizard) {
        try {
            doCreate(wizard)
        } catch (e: IOException) {
            UIUtil.invokeLaterIfNeeded {
                Messages.showErrorDialog(
                    e.message,
                    "Project Initialization Failed"
                )
            }
        }
    }

    @Throws(IOException::class)
    private fun doCreate(wizard: CommaAbstractProjectWizard) {
        val projectFilePath = wizard.newProjectFilePath
        val file = Paths.get(projectFilePath)
        for (p in getOpenProjects()) {
            if (isSameProject(file, p)) {
                focusProjectWindow(p, false)
                return
            }
        }

        val projectBuilder = wizard.projectBuilder
        LOG.debug("builder $projectBuilder")

        val projectManager = ProjectManagerEx.getInstanceEx()
        try {
            val projectDir: Path?
            if (wizard.storageScheme == StorageScheme.DEFAULT) {
                projectDir = file.parent
                if (projectDir == null) {
                    throw IOException("Cannot create project in '$projectFilePath': no parent file exists")
                }
            } else {
                projectDir = file
            }
            Files.createDirectories(projectDir)

            val newProject: Project?
            if (projectBuilder == null || !projectBuilder.isUpdate) {
                val name = wizard.projectName
                newProject = if (projectBuilder == null) {
                    projectManager.newProject(
                        file, OpenProjectTask.build()
                            .asNewProject()
                            .withProjectName(name)
                    )
                } else {
                    projectBuilder.createProject(name, projectFilePath)
                }
            } else {
                newProject = null
            }

            if (newProject == null) {
                return
            }

            val sdk = wizard.newProjectSdk
            if (sdk != null) {
                CommandProcessor.getInstance().executeCommand(
                    newProject,
                    {
                        ApplicationManager.getApplication()
                            .runWriteAction {
                                applyRakuSdkToProject(
                                    newProject,
                                    sdk
                                )
                            }
                    }, null, null
                )
            }

            if (projectBuilder != null) {
                // validate can require project on disk
                if (!ApplicationManager.getApplication().isUnitTestMode) {
                    newProject.save()
                }

                if (!projectBuilder.validate(null, newProject)) {
                    return
                }

                projectBuilder.commit(newProject, null, ModulesProvider.EMPTY_MODULES_PROVIDER)
            }

            if (!ApplicationManager.getApplication().isUnitTestMode) {
                val needToOpenProjectStructure = projectBuilder == null || projectBuilder.isOpenProjectSettingsAfter
                StartupManager.getInstance(newProject).runAfterOpened {
                    // ensure the dialog is shown after all startup activities are done
                    ApplicationManager.getApplication().invokeLater(
                        {
                            if (needToOpenProjectStructure) {
                                // FIXME re-instantiate Comma modules configurator showing
                                // ModulesConfigurator.showDialog(newProject, null, null);
                            }
                            ApplicationManager.getApplication()
                                .invokeLater(
                                    {
                                        val toolWindow = ToolWindowManager.getInstance(
                                            newProject
                                        )
                                            .getToolWindow(
                                                ToolWindowId.PROJECT_VIEW
                                            )
                                        toolWindow?.activate(
                                            null
                                        )
                                    },
                                    ModalityState.NON_MODAL,
                                    newProject.disposed
                                )
                        },
                        ModalityState.NON_MODAL,
                        newProject.disposed
                    )
                }
            }

            if (newProject != null) {
                updateLastProjectLocation(file)
                val options =
                    OpenProjectTask.build().withProject(newProject).withProjectName(file.fileName.toString())
                ProjectManagerEx.getInstanceEx().openProject(projectDir, options)
            }

            if (!ApplicationManager.getApplication().isUnitTestMode) {
                SaveAndSyncHandler.getInstance().scheduleProjectSave(newProject)
            }
        } finally {
            projectBuilder?.cleanup()
        }
    }

    @JvmStatic
    fun applyRakuSdkToProject(project: Project, rakuSdk: Sdk) {
        if (rakuSdk.homePath != null) {
            val service = project.getService(RakuSDKService::class.java)
            service.setProjectSdkPath(rakuSdk.homePath!!)
        }
    }

    @JvmStatic
    fun isRakudoCoreProject(project: Project): Boolean {
        return project.getService(RakuProjectDetailsService::class.java).isProjectRakudoCore()
    }

    // Technically this should also be available at the Facet level so that different project-modules can have their
    // own META6.json. Fix this later!
    fun projectDependencies(project: Project): List<String> {
        return metaFile(project).depends
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
 }
