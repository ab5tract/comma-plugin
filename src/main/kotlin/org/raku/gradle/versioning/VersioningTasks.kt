package org.raku.gradle.versioning

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

data class RakuPluginBetaVersion(val idea: String, val beta: Int) {
    override fun toString(): String { return "$idea-beta.$beta" }
}

abstract class IdeaVersion : DefaultTask() {
    @Input
    val ideaFileName: String = ".versions/idea-version"

    @InputFile
    val ideaVersionFile = File(ideaFileName)

    @TaskAction
    open fun action() {
        print(ideaVersionFile.readText())
    }
}


open class GetRakuPluginBetaVersion: IdeaVersion() {
    @Input
    val betaVersionFileName = ".versions/raku-beta-version"

    @OutputFile
    val betaVersionFile = File(betaVersionFileName)

    fun getPluginVersion(): RakuPluginBetaVersion {
//        val output = providers.exec { commandLine("git", "describe", "--tags") }
//            .standardOutput
//            .asText.get().trim()
        val output = "2024.3-beta.1"
        val lastBetaVersion = output.split(".").last().toInt()
        val lastIdeaBuildVersion = output.split("-").first()
        return RakuPluginBetaVersion(lastIdeaBuildVersion, lastBetaVersion)
    }

    @TaskAction
    override fun action() {
        betaVersionFile.parentFile.mkdirs()
        betaVersionFile.writeText(getPluginVersion().toString())
        println(getPluginVersion().toString())
    }
}

class BumpRakuPluginBetaVersion: GetRakuPluginBetaVersion() {
    @TaskAction
    override fun action() {
        val ideaVersion = ideaVersionFile.readText()
        val oldPluginVersion = getPluginVersion()

        val newPluginVersion = when (ideaVersion == oldPluginVersion.idea) {
            true  -> RakuPluginBetaVersion(oldPluginVersion.idea, oldPluginVersion.beta + 1)
            false -> RakuPluginBetaVersion(ideaVersion, 1)
        }

        betaVersionFile.parentFile.mkdirs()
        betaVersionFile.writeText(newPluginVersion.toString())
    }
}
