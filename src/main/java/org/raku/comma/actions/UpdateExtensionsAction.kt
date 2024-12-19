package org.raku.comma.actions

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.Function
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import net.miginfocom.swing.MigLayout
import java.awt.Dimension
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.swing.JComponent
import javax.swing.JPanel

class UpdateExtensionsAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project: Project = event.project ?: error("Action event not associated with a project: $event")
        val modules = ModuleManager.getInstance(project).modules

        val filesToUpdate: MutableMap<String, MutableList<File>> = collectFilesWithLegacyNames(modules)
        if (filesToUpdate.isEmpty()) {
            Notifications.Bus.notify(
                Notification(
                    "raku.messages",
                    "No legacy extensions detected",
                    NotificationType.INFORMATION
                )
            )
            return
        }

        RakuChooseExtensionsToUpdateDialog(project, filesToUpdate).show()
    }

    override fun update(event: AnActionEvent) {
        event.presentation.setEnabled(event.project != null)
    }

    class RakuChooseExtensionsToUpdateDialog(
        private val myProject: Project?,
        private val filesToUpdate: MutableMap<String, MutableList<File>>
    ) : DialogWrapper(
        myProject, true
    ) {
        private val exts: MutableList<StringItem?> = ContainerUtil.map<String, StringItem?>(
            filesToUpdate.keys, Function { file: String -> UpdateExtensionsAction.StringItem(file) })
        private var myTable: JBTable? = null
        private var myModel: ListTableModel<StringItem?>? = null


        init {
            init()
        }

        override fun createCenterPanel(): JComponent? {
            val panel = JPanel(MigLayout())
            myModel = ListTableModel<StringItem?>(
                arrayOf<ColumnInfo<*, *>>(
                    object : ColumnInfo<StringItem, Boolean>("Update") {
                        override fun valueOf(item: StringItem): Boolean {
                            return item.isSelected
                        }

                        override fun isCellEditable(item: StringItem?): Boolean {
                            return true
                        }

                        override fun getColumnClass(): Class<*> {
                            return Boolean::class.java
                        }

                        override fun setValue(item: StringItem, value: Boolean) {
                            item.isSelected = value
                        }
                    },
                    object : ColumnInfo<StringItem, String>("Name") {
                        override fun valueOf(item: StringItem): String {
                            return String.format(".%s -> .%s", item.ext, nonLegacyExts[item.ext])
                        }
                    }
                ), this.exts
            )
            myTable = JBTable(myModel)
            panel.minimumSize = Dimension(400, 300)
            panel.add(JBScrollPane(myTable), "growx, growy, pushx, pushy")
            return panel
        }

        override fun doOKAction() {
            val failedToProcess: MutableList<Pair<Path?, String?>> = ArrayList<Pair<Path?, String?>>()
            WriteCommandAction.runWriteCommandAction(myProject, "Renaming...", null, Runnable {
                for (i in 0..<myTable!!.getRowCount()) {
                    if (myTable!!.getValueAt(i, 0) is Boolean && (myTable!!.getValueAt(i, 0) as Boolean)) {
                        val ext = myModel!!.getItem(i)!!.ext
                        val files: MutableList<File> = filesToUpdate[ext]!!
                        for (file in files) {
                            val target = file.toPath()
                            val newName =
                                target.fileName.toString().replace(".$ext", "." + nonLegacyExts[ext])
                            try {
                                val vf = LocalFileSystem.getInstance().findFileByIoFile(file)
                                vf?.rename(this, newName)
                            } catch (e: IOException) {
                                failedToProcess.add(Pair.create<Path?, String?>(target, newName))
                            }
                        }
                    }
                }
            })
            LocalFileSystem.getInstance().refresh(false)
            close(0)
            if (!failedToProcess.isEmpty()) FailureDialog(failedToProcess).show()
        }

        private inner class FailureDialog(private val filesToShow: MutableList<Pair<Path?, String?>>) :
            DialogWrapper(myProject, true) {
            init {
                init()
            }

            override fun createCenterPanel(): JComponent? {
                val joiner = StringJoiner("<br>")
                joiner.add("Could not rename those files:<br>")
                filesToShow.forEach { pair ->
                    joiner.add(pair.first.toString() + " -> " + pair.second)
                }
                return JBLabel("<html>$joiner</html>")
            }
        }
    }

    private class StringItem(val ext: String) {
        var isSelected: Boolean = ext != "pod" && ext != "pm"
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    companion object {
        val FULL_LEGACY_EXTENSION_PATTERN: Pattern = Pattern.compile(".+?\\.(p6|pl6|pm6|pm|pod6|pod|t)")
        private val nonLegacyExts: MutableMap<String?, String?> = HashMap<String?, String?>()

        init {
            nonLegacyExts.put("p6", "raku")
            nonLegacyExts.put("pl6", "raku")
            nonLegacyExts.put("pm6", "rakumod")
            nonLegacyExts.put("pm", "rakumod")
            nonLegacyExts.put("pod6", "rakudoc")
            nonLegacyExts.put("pod", "rakudoc")
            nonLegacyExts.put("t", "rakutest")
        }

        fun collectFilesWithLegacyNames(modules: Array<Module>): MutableMap<String, MutableList<File>> {
            val filesToUpdate: MutableMap<String, MutableList<File>> = HashMap<String, MutableList<File>>()

            for (module in modules) {
                for (root in ModuleRootManager.getInstance(module).sourceRoots) {
                    if (root.isDirectory) {
                        val files = FileUtil.findFilesByMask(FULL_LEGACY_EXTENSION_PATTERN, root.toNioPath().toFile())
                        for (file in files) {
                            val matcher: Matcher = FULL_LEGACY_EXTENSION_PATTERN.matcher(file.getName())
                            if (matcher.matches()) {
                                val matchedFileKey = matcher.group(1)
                                val replaceList = filesToUpdate.computeIfAbsent(matchedFileKey) { mutableListOf() }
                                replaceList.add(file)
                            }
                        }
                    }
                }
            }
            return filesToUpdate
        }
    }
}
