//val ideaBuildVersion = "2024.3"
//data class RakuPluginVersion(val idea: String, val beta: Int) {
//    override fun toString(): String { return "$idea-beta.$beta" }
//}
//
//fun determineWorkingPluginVersion(): RakuPluginVersion {
//    val output = providers.exec { commandLine("git", "describe", "--tags") }
//        .standardOutput
//        .asText.get().trim()
//    val lastBetaVersion = output.split(".").last().toInt()
//    val lastIdeaBuildVersion = output.split("-").first()
//
//    return when (ideaBuildVersion == lastIdeaBuildVersion) {
//        true  -> RakuPluginVersion(lastIdeaBuildVersion, lastBetaVersion)
//        false -> RakuPluginVersion(ideaBuildVersion, 1)
//    }
//}
//
//tasks.register("retrieveVersion") {
//    group = "version"
//    description = "Retrieve plugin version"
//    print(determineWorkingPluginVersion())
//}
//
//tasks.register("bumpVersion") {
//    group = "version"
//    description = "Retrieve plugin version"
//
//    val oldVersion = determineWorkingPluginVersion()
//    print(RakuPluginVersion(oldVersion.idea, oldVersion.beta + 1))
//}