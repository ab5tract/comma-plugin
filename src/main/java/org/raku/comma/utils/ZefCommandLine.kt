package org.raku.comma.utils

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.raku.comma.pm.RakuPackageManagerManager
import javax.swing.JTextPane

class ZefCommandLine(project: Project) : GeneralCommandLine() {
    private val zefLocation = project.service<RakuPackageManagerManager>().state.location

    init {
        if (zefLocation != null) {
            exePath = zefLocation
        } else {
            val location = project.service<RakuPackageManagerManager>().detectPMs().firstOrNull()
            if (location != null) {
                exePath = location
            }
        }
    }
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