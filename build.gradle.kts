import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij.platform") version "2.0.1"
     id("org.jetbrains.grammarkit") version "2022.3.2.2"

    kotlin("jvm") version "2.0.20"
}

group = properties("pluginGroup")
version = "2.0"

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

sourceSets {
  main {
    java {
      srcDirs("src/main/gen")
    }
  }
}

intellijPlatform {
    pluginConfiguration {
        id = "org.raku.comma"
        name = "Comma for Raku"
        version = "2.0"
    }

    pluginVerification {
        ides {
            ide(IntelliJPlatformType.IntellijIdeaCommunity, "2024.2")
        }
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.2")

        bundledPlugin("com.intellij.java")

        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
//    implementation(kotlin("stdlib-jdk8"))
    implementation(files("libs/xchart-3.8.0.jar"))
    implementation(files("libs/moarvmremote.jar"))
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