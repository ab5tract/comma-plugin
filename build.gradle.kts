
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import kotlin.io.path.Path
import kotlin.io.path.exists


fun properties(key: String) = project.findProperty(key).toString()

// TODO: Don't include all of this mess in one file
//////// VERSION STUFF

fun formatBranch(
    gitBranch: String,
    format: String = "%s"
) = if (gitBranch != "main") format.format(gitBranch) else ""

val ideaBuildVersion = File(".versions/idea-version").readText(Charsets.UTF_8)

fun determineCurrentGitBranch(): String {
    return  providers.exec {
                     commandLine("git", "branch", "--show-current")
            }.standardOutput.asText.get().trim().lines().last()
}

fun gitCurrentRakuBetaPluginVersion(): String {
    return providers.exec {
                 commandLine("git", "tag", "--merged", "main", "--sort=taggerdate")
             }.standardOutput.asText.get().trim().lines().last()
}

fun safeDetermineCurrentRakuBetaPluginVersion(currentGitBranch: String): String {
    val betaVersionPath = Path(".versions/raku-beta-version${ formatBranch(currentGitBranch, ".%s") }")

    return when(betaVersionPath.exists()) {
        true  -> betaVersionPath.toFile().readText().trim()
        false -> {
            val idea = File(".versions/idea-version").readText(Charsets.UTF_8)
            "$idea-beta${formatBranch(currentGitBranch, "(%s)") }.1"
        }
    }
}

abstract class IdeaVersionTask : DefaultTask() {
    @Input
    val ideaFileName: String = ".versions/idea-version"

    @InputFile
    val ideaVersionFile = File(ideaFileName)

    @Input
    val ideaVersion: String = ideaVersionFile.readText()

    @TaskAction
    open fun action() {
        println(ideaVersion)
    }
}

data class RakuPluginBetaVersion(val idea: String, val beta: Int, val branch: String) {
    fun fileName(): String = ".versions/raku-beta-version${ maybeBranch(".%s") }"
    fun maybeBranch(format: String = "%s") = if (branch != "main") format.format(branch) else ""

    override fun toString(): String = "$idea-beta${ maybeBranch("(%s)") }.$beta"
}

// TODO: Make this support branches other that 'main'
abstract class FetchGitTagRakuPluginBetaVersion : IdeaVersionTask() {
    @get:Input
    val gitBranch: Property<String> = project.objects.property<String>()

    @get:Input
    val gitTag: Property<String> = project.objects.property<String>()

    @Internal
    val version = gitTag.map { it.last().digitToInt() }
    @Internal
    val pluginBetaVersion: Provider<RakuPluginBetaVersion> = version.map { determinePluginVersion(it) }

    fun determinePluginVersion(version: Int): RakuPluginBetaVersion
                =   RakuPluginBetaVersion(
                        idea   = ideaVersion,
                        beta   = version,
                        branch = gitBranch.get())

    @TaskAction
    override fun action() {
        println(pluginBetaVersion.get().toString())
    }
}

abstract class GetRakuPluginBetaVersion : IdeaVersionTask() {
    @get:Input
    val gitBranch: Property<String> = project.objects.property<String>()

    @get:Input
    val gitTag: Property<String> = project.objects.property<String>()

    @Internal
    val version = gitTag.map { it.last().digitToInt() }
    @Internal
    val pluginBetaVersion: Provider<RakuPluginBetaVersion> = version.map { determinePluginVersion(it) }

    @get:OutputFile
    val betaVersionFile: Provider<File> = pluginBetaVersion.map { File(it.fileName()) }

    fun determinePluginVersion(version: Int): RakuPluginBetaVersion
                =   RakuPluginBetaVersion(
                        idea   = ideaVersion,
                        beta   = version,
                        branch = gitBranch.get())

    @TaskAction
    override fun action() {
        betaVersionFile.get().parentFile.mkdirs()
        betaVersionFile.get().writeText(pluginBetaVersion.get().toString())
        println(pluginBetaVersion.get().toString())
    }
}

abstract class BumpRakuPluginBetaVersion: GetRakuPluginBetaVersion() {
    @TaskAction
    override fun action() {
        val oldPluginVersion = pluginBetaVersion.get()

        val newPluginVersion = when (ideaVersion == oldPluginVersion.idea) {
            true  -> RakuPluginBetaVersion(oldPluginVersion.idea,
                                           oldPluginVersion.beta + 1,
                                           gitBranch.get())
            false -> RakuPluginBetaVersion(ideaVersion, 1, gitBranch.get())
        }

        betaVersionFile.get().writeText(newPluginVersion.toString())
        println(newPluginVersion)
    }
}
////// END VERSION STUFF

tasks.register<IdeaVersionTask>("retrieveIdeaVersion") {
    group = "version"
    description = "Retrieve IntelliJ IDEA version"
}

tasks.register<FetchGitTagRakuPluginBetaVersion>("findVersionFromGitTag") {
    group = "version"
    description = "Determine plugin beta version based on git tags"

    gitTag = gitCurrentRakuBetaPluginVersion()
    gitBranch = "main"
}

tasks.register<GetRakuPluginBetaVersion>("retrieveBetaVersion") {
    group = "version"
    description = "Retrieve plugin beta version"

    gitTag = safeDetermineCurrentRakuBetaPluginVersion(currentGitBranch = determineCurrentGitBranch())
    gitBranch = determineCurrentGitBranch()
}

tasks.register<BumpRakuPluginBetaVersion>("bumpBetaVersion") {
    group = "version"
    description = "Bump plugin beta version"

    gitTag = safeDetermineCurrentRakuBetaPluginVersion(currentGitBranch = determineCurrentGitBranch())
    gitBranch = determineCurrentGitBranch()
}

plugins {
    // Java support
    id("java")
    // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij.platform") version "2.0.1"

    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
}

group   = properties("pluginGroup")
version = safeDetermineCurrentRakuBetaPluginVersion(determineCurrentGitBranch())
// Configure project's dependencies
repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

intellijPlatform {
    pluginConfiguration {
        id = "org.raku.comma"
        name = "Raku"
        version = safeDetermineCurrentRakuBetaPluginVersion(determineCurrentGitBranch())

        ideaVersion {
            sinceBuild = "242"
        }
    }

    pluginVerification {
        ides {
            select {
                types = listOf(IntelliJPlatformType.values().toList().random())
                channels = listOf(ProductRelease.Channel.RELEASE)
                sinceBuild = "242"
            }
        }
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(ideaBuildVersion)

        bundledPlugin("com.intellij.java")

        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
    implementation(files("libs/xchart-3.8.0.jar"))
    implementation(files("libs/moarvmremote.jar"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation("io.airlift:aircompressor:2.0.2")
    implementation("org.json:json:20240303")
    // TODO: Remove this due to multiple unpatch CVEs
    implementation("org.tap4j:tap4j:4.4.2")
}

tasks {
    instrumentCode {
        formsDirs = files("src/main/org/raku/comma/project/projectWizard/components")
    }
}