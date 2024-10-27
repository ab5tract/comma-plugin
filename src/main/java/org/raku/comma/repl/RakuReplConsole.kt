package org.raku.comma.repl

import com.intellij.execution.ExecutionException
import com.intellij.execution.Executor
import com.intellij.execution.console.ConsoleExecuteAction
import com.intellij.execution.console.LanguageConsoleBuilder
import com.intellij.execution.console.LanguageConsoleView
import com.intellij.execution.console.ProcessBackedConsoleExecuteActionHandler
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import org.raku.comma.RakuLanguage
import org.raku.comma.utils.RakuCommandLine
import org.raku.comma.utils.RakuUtils
import java.awt.Font
import java.io.File

class RakuReplConsole(
    project: Project,
    consoleTitle: String
) : AbstractConsoleRunnerWithHistory<LanguageConsoleView>(project, consoleTitle, project.basePath) {
    private var commandLine: RakuCommandLine? = null
    @JvmField
    val replState: RakuReplState = RakuReplState(this)
    private var myReplBackendFile: File? = null

    override fun createConsoleView(): LanguageConsoleView {
        val builder = LanguageConsoleBuilder()
        builder.oneLineInput(false)
        val consoleView = builder
            .build(project, RakuLanguage.INSTANCE)

        val consoleEditor = consoleView.consoleEditor
        addHint(consoleEditor)
        consoleEditor.settings.isCaretRowShown = true
        consoleEditor.contentComponent.addKeyListener(RakuReplHistoryKeyListener(this))

        return consoleView
    }

    override fun finishConsole() {
        super.finishConsole()
        myReplBackendFile!!.delete()
    }

    @Throws(ExecutionException::class)
    override fun createProcess(): Process? {
        myReplBackendFile = RakuUtils.getResourceAsFile("repl/repl-backend.raku")
        commandLine = RakuCommandLine(project)
        commandLine!!.setWorkDirectory(project.basePath)
        commandLine!!.addParameter("-I.")
        commandLine!!.addParameter(myReplBackendFile!!.absolutePath)
        return commandLine!!.createProcess()
    }

    override fun createProcessHandler(process: Process): OSProcessHandler {
        return RakuReplOutputHandler(process, commandLine!!.commandLineString, this)
    }

    override fun createExecuteActionHandler(): ProcessBackedConsoleExecuteActionHandler {
        return object : ProcessBackedConsoleExecuteActionHandler(processHandler, true) {
            override fun beforeExecution(view: LanguageConsoleView) {
                // Ensure that the current REPL document ends in a newline before adding the text of
                // the command.
                val currentText = view.historyViewer.document.text
                if (!currentText.endsWith("\n")) {
                    view.print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
                }
                (view as ConsoleViewImpl).flushDeferredText()

                super.beforeExecution(view)
            }

            override fun processLine(line: String) {
                // Wrap in envelope for REPL backend.
                val lines = line.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                sendText(
                    """
                        EVAL ${lines.size}
                        ${java.lang.String.join("\n", *lines)}

                    """.trimIndent()
                )

                // Add this line to the history, (used for history and auto-complete).
                replState.addExecuted(line)
            }
        }
    }

    override fun createConsoleExecAction(consoleExecuteActionHandler: ProcessBackedConsoleExecuteActionHandler): AnAction {
        return ConsoleExecuteAction(
            consoleView,
            consoleExecuteActionHandler,
            "RakuReplExecute",
            consoleExecuteActionHandler
        )
    }

    override fun fillToolBarActions(
        toolbarActions: DefaultActionGroup,
        defaultExecutor: Executor,
        contentDescriptor: RunContentDescriptor
    ): List<AnAction> {
        val actionList: MutableList<AnAction> = ArrayList()
        actionList.add(createCloseAction(defaultExecutor, contentDescriptor))
        actionList.add(createConsoleExecAction(consoleExecuteActionHandler))
        toolbarActions.addAll(actionList)
        return actionList
    }

    fun executeStatement(command: String) {
        ApplicationManager.getApplication().runWriteAction {
            consoleView.editorDocument.setText(command)
            consoleExecuteActionHandler.runExecuteAction(consoleView)
        }
    }

    companion object {
        private fun addHint(editor: EditorEx) {
            val executeCommandAction = ActionManager.getInstance().getAction("RakuReplExecute")
            val executeCommandActionShortcutText = KeymapUtil.getFirstKeyboardShortcutText(executeCommandAction)
            editor.setPlaceholder("<$executeCommandActionShortcutText> to execute")
            editor.setShowPlaceholderWhenFocused(true)

            val placeholderAttrs = TextAttributes()
            placeholderAttrs.foregroundColor = JBColor.LIGHT_GRAY
            placeholderAttrs.fontType = Font.ITALIC
            editor.setPlaceholderAttributes(placeholderAttrs)
        }
    }
}
