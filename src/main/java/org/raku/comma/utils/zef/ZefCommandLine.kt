package org.raku.comma.utils.zef

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.raku.comma.services.project.RakuProjectSdkService
import java.nio.file.Paths
import javax.swing.JTextPane

class ZefCommandLine(project: Project, type: CommandLineTarget = CommandLineTarget.ZEF) : GeneralCommandLine() {

    init {
        val sdkService = project.service<RakuProjectSdkService>()

        exePath = when (type) {
            CommandLineTarget.ZEF,
            CommandLineTarget.RAKU -> sdkService.rakuPath ?: throw RuntimeException("No Raku binary found in specified SDK path '${sdkService.sdkPath}'")
            CommandLineTarget.GIT  -> detectExecutables(listOf("git")).firstOrNull() ?: throw RuntimeException("Git executable not found in PATH")
        }

        if (type == CommandLineTarget.ZEF) {
            addParameters("-MZef::CLI", "-e''")
        }
    }

    val isSetup: Boolean
        get() = exePath.isNotBlank()
}

interface ZefCommandLineOutput {
    fun addFirst(line: String)
    fun addLast(line: String)

    fun addText(line: String)
    fun addText(lines: List<String>)
}

class ZefCommandLineOutputTextPane(private val outputPane: JTextPane) : ZefCommandLineOutput {
    override fun addFirst(line: String) {
        outputPane.text += "\n$line"
    }

    override fun addLast(line: String) {
        outputPane.text += "\n\n$line\n\n"
    }

    override fun addText(lines: List<String>) {
        outputPane.text += rollUpOutput(lines)
    }

    override fun addText(line: String) {
        outputPane.text += rollUpOutput(line)
    }

    private fun rollUpOutput(newLines: List<String>): String {
        var textSum = ""
        newLines.forEach { line ->
            textSum += rollUpOutput(line)
        }
        return textSum
    }

    private fun rollUpOutput(newLine: String): String {
        return  """
                ${newLine.trim()}
                """.trimEnd()
    }
}

fun detectExecutables(executables: List<String>) : List<String> {
    val detected = mutableListOf<String>()
    for (name in executables) {
        val paths = System.getenv("PATH").split(":".toRegex())
            .dropLastWhile { it.isEmpty() }
        for (path in paths) {
            val pathWithName = Paths.get(path, name)
            val file = pathWithName.toFile()
            if (file.exists() && file.canExecute()) {
                detected.add(pathWithName.toString())
            }
        }
    }
    return detected
}

enum class CommandLineTarget(val value: String) {
    ZEF("zef"),
    GIT("git"),
    RAKU("raku")
}