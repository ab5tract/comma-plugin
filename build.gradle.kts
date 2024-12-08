import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease
import kotlin.io.path.Path
import kotlin.io.path.exists


fun properties(key: String) = project.findProperty(key).toString()

// TODO: Don't include all of this mess in one file
//////// VERSION STUFF
val ideaBuildVersion = File(".versions/idea-version").readText(Charsets.UTF_8)

fun determineCurrentRakuBetaPlugin(): String {
    val betaVersionPath = Path(".versions/raku-beta-version")

    return when(betaVersionPath.exists()) {
        true  -> betaVersionPath.toFile().readText().trim()
        false -> providers.exec {
                     commandLine("git", "tag", "--merged", "main", "--sort=taggerdate")
                 }.standardOutput.asText.get().trim().lines().last()
    }
}
val currentRakuPluginVersion = determineCurrentRakuBetaPlugin()

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

abstract class GetRakuPluginBetaVersion : IdeaVersionTask() {
    data class RakuPluginBetaVersion(val idea: String, val beta: Int) {
        override fun toString(): String { return "$idea-beta.$beta" }
    }

    @get:Input
    abstract var gitTag: String

    @Input
    val betaVersionFileName = ".versions/raku-beta-version"

    @OutputFile
    val betaVersionFile = File(betaVersionFileName)

    fun determinePluginVersion(): RakuPluginBetaVersion {
        val lastBetaVersion = gitTag.split(".").last().toInt()
        val lastIdeaBuildVersion = gitTag.split("-").first()
        return RakuPluginBetaVersion(lastIdeaBuildVersion, lastBetaVersion)
    }

    @TaskAction
    override fun action() {
        betaVersionFile.parentFile.mkdirs()
        betaVersionFile.writeText(determinePluginVersion().toString())
        println(determinePluginVersion().toString())
    }
}

abstract class BumpRakuPluginBetaVersion: GetRakuPluginBetaVersion() {
    @TaskAction
    override fun action() {
        val oldPluginVersion = determinePluginVersion()

        val newPluginVersion = when (ideaVersion == oldPluginVersion.idea) {
            true  -> RakuPluginBetaVersion(oldPluginVersion.idea, oldPluginVersion.beta + 1)
            false -> RakuPluginBetaVersion(ideaVersion, 1)
        }

        betaVersionFile.writeText(newPluginVersion.toString())
        println(newPluginVersion)
    }
}
////// END VERSION STUFF

tasks.register<IdeaVersionTask>("retrieveIdeaVersion") {
    group = "version"
    description = "Retrieve IntelliJ IDEA version"
}

tasks.register<GetRakuPluginBetaVersion>("retrieveBetaVersion") {
    group = "version"
    description = "Retrieve plugin beta version"
    gitTag = currentRakuPluginVersion
}

tasks.register<BumpRakuPluginBetaVersion>("bumpBetaVersion") {
    group = "version"
    description = "Bump plugin beta version"
    gitTag = currentRakuPluginVersion
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
version = currentRakuPluginVersion
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
        version = currentRakuPluginVersion

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