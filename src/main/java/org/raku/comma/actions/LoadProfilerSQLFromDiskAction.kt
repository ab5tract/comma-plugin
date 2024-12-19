package org.raku.comma.actions

import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Condition
import com.intellij.openapi.vfs.VirtualFile
import org.raku.comma.profiler.run.RakuImportRunner
import org.raku.comma.run.RakuProfileExecutor

class LoadProfilerSQLFromDiskAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project: Project = event.project
                ?: error("No project associated with this action event: $event")
        val sqlDescriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
            .withFileFilter(Condition { vf: VirtualFile? -> vf!!.name.endsWith(".sql") })
        sqlDescriptor.title = "Choose a File with SQL Data from the Raku Profiler"
        val file = FileChooser.chooseFile(sqlDescriptor, project, null)
        if (file == null) return

        executeTheFile(project, file)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    companion object {
        private fun executeTheFile(project: Project, file: VirtualFile) {
            try {
                val profile = RakuImportRunner(file)
                val executor: Executor = RakuProfileExecutor()
                val builder = ExecutionEnvironmentBuilder.create(project, executor, profile)
                builder.buildAndExecute()
            } catch (ex: ExecutionException) {
                Messages.showErrorDialog(project, ex.message, "Import Failed")
            }
        }
    }
}
