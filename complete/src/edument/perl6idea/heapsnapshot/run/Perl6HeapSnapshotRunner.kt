package edument.perl6idea.heapsnapshot.run

import com.intellij.execution.ExecutionException
import com.intellij.execution.ExecutionManager.Companion.getInstance
import com.intellij.execution.ExecutionResult
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.fileEditor.FileDocumentManager
import edument.perl6idea.heapsnapshot.ui.HeapSnapshotContentBuilder
import edument.perl6idea.run.Perl6HeapSnapshotExecutor
import edument.perl6idea.run.Perl6RunConfiguration
import org.jetbrains.concurrency.resolvedCancellablePromise

class Perl6HeapSnapshotRunner : ProgramRunner<RunnerSettings> {
    override fun getRunnerId(): String {
        return "Raku Heap Snapshot"
    }

    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return Perl6HeapSnapshotExecutor.EXECUTOR_ID == executorId &&
               profile is Perl6RunConfiguration
    }

    @Throws(ExecutionException::class)
    override fun execute(environment: ExecutionEnvironment) {
        val state = environment.state ?: return
        getInstance(environment.project).startRunProfile(environment) { resolvedCancellablePromise(doExecute(state, environment)) }
    }

    @Throws(ExecutionException::class)
    private fun doExecute(state: RunProfileState, env: ExecutionEnvironment): RunContentDescriptor? {
        FileDocumentManager.getInstance().saveAllDocuments()
        val executionResult = state.execute(env.executor, this)
        return showRunContent(executionResult, env, state as Perl6HeapSnapshotCommandLineState)
    }

    companion object {
        @Throws(ExecutionException::class)
        private fun showRunContent(result: ExecutionResult?,
                                   env: ExecutionEnvironment,
                                   state: Perl6HeapSnapshotCommandLineState): RunContentDescriptor? {
            return if (result != null) HeapSnapshotContentBuilder(result, env).showRunContent(env.contentToReuse, state) else null
        }
    }
}