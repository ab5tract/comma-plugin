package org.raku.debugger

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.runners.RunContentBuilder
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.fileEditor.FileDocumentManager

abstract class RakuDefaultRunner : GenericProgramRunner<RunnerSettings>() {
    @Throws(ExecutionException::class)
    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        FileDocumentManager.getInstance().saveAllDocuments()
        val execResult = state.execute(environment.executor, this)
        return if (execResult != null) {
            RunContentBuilder(execResult, environment).showRunContent(environment.contentToReuse)
        } else {
            null
        }
    }
}