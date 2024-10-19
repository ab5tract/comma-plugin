package org.raku.comma.services.project

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.raku.comma.RakuIcons
import org.raku.comma.metadata.data.MetaFile
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference

@Service(Service.Level.PROJECT)
class RakuMetaDataComponent(private val project: Project, val runScope: CoroutineScope) {
    var metaFile: VirtualFile? = null
        private set

    private var meta: MetaFile? = null

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
        allowTrailingComma = true
    }

    var metaLoaded: CompletableFuture<Boolean>? = null
    private fun eventPredicate (event: VFileEvent): Boolean {
        if (event.file == null) return false
        val fileName = event.file!!.name
        return event.isFromSave && fileName == META6_JSON_NAME || fileName == META_OBSOLETE_NAME
    }

    init {
       project.messageBus.connect().subscribe(VirtualFileManager.VFS_CHANGES, object: BulkFileListener {
            override fun after(events: List<VFileEvent>) {
                events.filter(::eventPredicate).forEach { _ ->
                    meta = checkMetaSanity();
                    saveFile();
                    if (meta != null) {
                        refreshDependencyState()
                    }
                }
            }
        })

        if (project.basePath != null) {
            val metaParent: VirtualFile = VirtualFileManager.getInstance().findFileByNioPath(Path.of(project.basePath!!))!!
            var metaFile: VirtualFile? = metaParent.findChild(META6_JSON_NAME)
            // Try to search by obsolete 'META.info' name and warn about it if present
            if (metaFile == null) {
                metaFile = checkOldMetaFile(metaParent)

                // If everything fails, notify about META absence
                // and suggest stubbing it
                if (metaFile == null) {
                    notifyMissingMETA()
                }
            }

            if (metaFile != null) {
                this.metaFile = metaFile

                metaLoaded = runScope.future {
                    meta = runReadAction {
                        try {
                            return@runReadAction json.decodeFromString(metaFile!!.readText())
                        } catch (e: Exception) {
                            notifyMetaIssue("Could not parse META6.json: ${e.message}", NotificationType.WARNING)
                        }
                        return@runReadAction null
                    }
                    return@future meta != null
                }
            }
        }
        if (metaLoaded == null) {
            metaLoaded = CompletableFuture.completedFuture(false)
        }
    }

    val module: Module?
        get() = Arrays.stream(ModuleManager.getInstance(project).modules)
            .findFirst()
            .orElse(null)

    private fun checkOldMetaFile(metaParent: VirtualFile): VirtualFile? {
        val metaFile: VirtualFile? = metaParent.findChild(META_OBSOLETE_NAME)
        if (metaFile != null) {
            notifyMetaIssue(
                String.format("Obsolete '%s' file name is used instead of '%s'", META_OBSOLETE_NAME, META6_JSON_NAME),
                NotificationType.ERROR,
                object : AnAction(String.format("Rename to %s", META6_JSON_NAME)) {
                    override fun actionPerformed(event: AnActionEvent) {
                        runWriteAction {
                            try {
                                metaFile.rename(this, META6_JSON_NAME)
                            } catch (ex: IOException) {
                                Notifications.Bus.notify(
                                    Notification(
                                        "raku.meta.errors", "Raku META error",
                                        "Could not rename META file: " + ex.message,
                                        NotificationType.ERROR
                                    )
                                )
                            }
                        }
                    }
                })
        }
        return metaFile
    }

    private fun checkMetaSanity(): MetaFile? {
        var meta: MetaFile? = null
        if (metaFile != null && metaFile!!.exists()) {
            try {
                meta = runReadAction {
                    json.decodeFromString(metaFile!!.readText())
                }
                // TODO: If this fails, we should try and parse as an ExternalMetaFile
                // and then convert
                //checkMetaSanity(meta)
            } catch (e: RakuMetaException) {
                notifyMetaIssue(e.message!!, NotificationType.ERROR, e.myFix!!)
            } catch (e: IOException) {
                notifyMetaIssue(e.message!!, NotificationType.ERROR)
            } catch (e: Exception) {
                notifyMetaIssue(e.message!!, NotificationType.ERROR)
            }
        }
        return meta
    }

    val resources: List<String>
        get() = meta?.resources ?: listOf()

    fun removeResource(name: String) {
        if (meta == null) return
        val newResources = resources.filterNot { it == name  }
        meta = meta!!.copy(resources = newResources)
        saveFile()
    }

    fun addResource(newName: String) {
        if (meta == null) return
        val newResources = meta!!.resources.toMutableList().apply { add(newName) }
        meta = meta!!.copy(resources = newResources)
        saveFile()
    }

    fun triggerMetaBuild() {
        val metaParent: VirtualFile = calculateMetaParent() ?: return
        val metaFile: VirtualFile? = metaParent.findChild(META6_JSON_NAME)
        if (metaFile != null) {
            triggerMetaBuild(metaFile)
        }
    }

    fun triggerMetaBuild(metaFile: VirtualFile) {
        this.metaFile = metaFile
        meta = checkMetaSanity()
        refreshDependencyState()
    }

    val noMeta: Boolean
        get() = meta == null

    private fun refreshDependencyState() {
        val projectService = project.service<RakuProjectDetailsService>()
        if (projectService.moduleServiceDidStartup) {
            projectService.moduleServiceDidStartup = false
            project.service<RakuDependencyService>().initialize()
        }
    }

    // It did not feel worth DRY-ing these two cases. Three, yes, two, no.
    fun addDepends(name: String) {
        if (noMeta) return

        val newDepends = meta!!.depends.toMutableList().apply { add(name) }.toList()
        meta = meta!!.copy(depends = newDepends)
        saveFile()
        refreshDependencyState()
    }

    fun addTestDepends(name: String) {
        if (noMeta) return

        val newDepends = meta!!.testDepends.toMutableList().apply { add(name) }.toList()
        meta = meta!!.copy(testDepends = newDepends)
        saveFile()
        refreshDependencyState()
    }

    fun addBuildDepends(name: String) {
        // TODO: Wire this up. The structure for build dependencies is stupidly complex.
        // Also, Old Comma didn't support this at all!
        if (noMeta) return
    }

    val allDependencies: Set<String>
        get() = meta?.allDepends?.toSet() ?: setOf()

    val depends: List<String>
        get() =  meta?.depends ?: listOf()

    // More never-used functionality in Old Comma
    val testDepends: List<String>
        get() = meta?.testDepends ?: listOf()

    val buildDepends: List<String>
        get() = meta?.simplifiedBuildDepends ?: listOf()

    val providedMap: Map<String, String>
        get() = meta?.provides ?: mapOf()

    val providedNames: Collection<String>
        get() = providedMap.keys

    @Throws(IOException::class)
    fun createStubMetaFile(moduleName: String, firstRoot: VirtualFile?, shouldOpenEditor: Boolean) {
        var firstRoot: VirtualFile? = firstRoot
        if (firstRoot == null) {
            firstRoot = calculateMetaParent()
        }
        if (firstRoot == null) {
            val file: VirtualFile?
            if (module != null) {
                val entries: Array<ContentEntry> = ModuleRootManager.getInstance(module!!).contentEntries
                file = FileChooser.chooseFile(
                    FileChooserDescriptorFactory.createSingleFolderDescriptor(),
                    project,
                    if (entries.size == 1 && entries.first().file != null) entries.first().file else null
                )
            } else {
                file =
                    FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), project, null)
            }

            if (file == null) {
                notifyMetaIssue(
                    "Directory was not selected, meta file creation is canceled",
                    NotificationType.INFORMATION
                )
                return
            } else {
                firstRoot = file
            }
        }

        val ex: AtomicReference<IOException?> = AtomicReference()
        ex.set(null)
        val finalFirstRoot: VirtualFile = firstRoot
        ApplicationManager.getApplication().invokeAndWait({
            WriteAction.run<RuntimeException>({
                try {
                    val meta: MetaFile = getStubMetaObject(moduleName)
                    val metaFile: VirtualFile = finalFirstRoot.findOrCreateChildData(this, META6_JSON_NAME)
                    metaFile.setBinaryContent(json.encodeToString(meta).toByteArray(StandardCharsets.UTF_8))

                    this.meta = meta
                    this.metaFile = metaFile
                  if (shouldOpenEditor) {
                      FileEditorManager.getInstance(project).openFile(metaFile, true)
                  }
                } catch (e: IOException) {
                  ex.set(e)
                }
            })
        })
        val ioException: IOException? = ex.get()
        if (ioException != null) {
            throw ioException
        }
    }

    private fun calculateMetaParent(): VirtualFile? {
        if (module == null) return null

        val sourceRoots: Array<VirtualFile> = ModuleRootManager.getInstance(module!!).sourceRoots
        for (root: VirtualFile in sourceRoots) {
            if (root.name != "lib") continue
            return root.parent
        }

        return null
    }

    fun addNamespaceToProvides(name: String, ext: String?) {
        if (noMeta) return

        val libBasedModulePath: String = String.format("lib/%s.%s", name.replace("::".toRegex(), "/"), ext)
        val newProvides = meta!!.provides.toMutableMap().apply { put(name, libBasedModulePath) }
        meta = meta!!.copy(provides = newProvides)
        saveFile()
    }

    fun removeNamespaceFromProvides(name: String?) {
        if (noMeta || name == null) return
        val newProvides = meta!!.provides.toMutableMap().apply { remove(name) }
        meta = meta!!.copy(provides = newProvides)
        saveFile()
    }

    var name: String?
        get() = meta?.name
        set(name) {
            if (noMeta) return
            meta = meta!!.copy(name = name)
            saveFile()
        }

    var description: String?
        get() = meta?.description
        set(description) {
            if (noMeta) return
            meta = meta!!.copy(description = description)
            saveFile()
        }

    var version: String?
        get() = meta?.version
        set(version) {
            if (noMeta) return
            meta = meta!!.copy(version = version)
            saveFile()
        }

    var auth: String?
        get() = meta?.auth
        set(auth) {
            if (noMeta) return
            meta = meta!!.copy(auth = auth)
            saveFile()
        }

    var license: String?
        get() = meta?.license
        set(license) {
            if (meta == null) return
            meta = meta!!.copy(license = license)
            saveFile()
        }

    var sourceUrl: String?
        get() = meta?.sourceUrl
        set(sourceUrl) {
            if (noMeta) return
            meta = meta!!.copy(sourceUrl = sourceUrl)
            saveFile()
        }

    var authors: List<String>?
        get() = meta?.authors
        set(authors) {
            if (noMeta || authors == null) return
            meta = meta!!.copy(authors = authors)
            saveFile()
        }

    @Synchronized
    private fun saveFile() {
        if (metaFile == null || meta == null) return

        runWriteAction {
            try {
                if (!metaFile!!.isValid) metaFile =
                    metaFile!!.parent.createChildData(
                        this,
                        metaFile!!.name
                    )
                metaFile!!.setBinaryContent(
                    json.encodeToString(meta)
                        .toByteArray(StandardCharsets.UTF_8)
                )
                triggerMetaBuild(metaFile!!)
            } catch (e: IOException) {
                notifyMetaIssue(
                    e.message!!,
                    NotificationType.ERROR
                )
            }
        }
    }

    private fun notifyMetaIssue(message: String, type: NotificationType, vararg actions: AnAction) {
        if (module == null) return

        val notification = Notification("raku.meta.errors", "Raku meta error", message, type)
        notification.setIcon(RakuIcons.CAMELIA)
        if (metaFile != null) {
            notification.addAction(object : AnAction(String.format("Open %s", META6_JSON_NAME)) {
                override fun actionPerformed(e: AnActionEvent) {
                    if (project.isDisposed) return
                    FileEditorManager.getInstance(project).openFile(metaFile!!, true)
                    notification.expire()
                }
            })
        }
        for (action: AnAction? in actions) {
            if (action == null) continue
            notification.addAction(object : AnAction(action.templatePresentation.text) {
                override fun actionPerformed(e: AnActionEvent) {
                    notification.expire()
                    action.actionPerformed(e)
                }
            })
        }
        Notifications.Bus.notify(notification, project)
    }

    private fun notifyMissingMETA() {
        val notification: Notification = Notification(
            "raku.meta.errors", "Raku meta file is missing",
            String.format(
                "'%s' nor '%s' files seem to be present in this module.",
                META_OBSOLETE_NAME,
                META6_JSON_NAME
            ),
            NotificationType.WARNING
        )
        notification.setIcon(RakuIcons.CAMELIA)
        notification.addAction(object : AnAction(String.format("Stub and open %s file", META6_JSON_NAME)) {
            override fun actionPerformed(e: AnActionEvent) {
                try {
                    notification.expire()
                    if (module == null || module?.isDisposed == true || project.isDisposed) return
                    createStubMetaFile(module!!.getName(), null, true)
                } catch (e1: IOException) {
                    val notification1: Notification = Notification(
                        "raku.meta.errors", String.format("%s error", META6_JSON_NAME),
                        e1.message!!, NotificationType.ERROR
                    )
                    notification1.setIcon(RakuIcons.CAMELIA)
                    notification1.setSubtitle(
                        String.format(
                            "Error has occurred during %s file creation",
                            META6_JSON_NAME
                        )
                    )
                    Notifications.Bus.notify(notification1)
                }
            }
        })
        Notifications.Bus.notify(notification)
    }

    private class RakuMetaException @JvmOverloads constructor(message: String?, val myFix: AnAction? = null) :
        Exception(message)

    companion object {
        const val META6_JSON_NAME: String = "META6.json"
        const val META_OBSOLETE_NAME: String = "META.info"

        private fun getStubMetaObject(moduleName: String): MetaFile {
            val authorsArray: JSONArray = JSONArray()
            authorsArray.put("Write me!")
            return MetaFile(
                raku = "6.*",
                name = "moduleName",
                version = "0.1",
                description = "Write me!",
                auth = "Write me!",
                license = "Write me!",
                sourceUrl = "Write me!"
            )
        }
    }
}
