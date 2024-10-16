package org.raku.comma.metadata.data

import com.intellij.openapi.components.service
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import org.raku.comma.services.application.RakuDistroInfo

// This class is for all projects created or opened by Comma plugin.
// In order to achieve a semblance of sanity, we will unapologetically
// adjust your META6.json to the reasonable version.

@Serializable
data class MetaFile (
    val name: String? = null,
    val description: String? = null,
    val version: String? = null,
    val raku: String? = null,
    @SerialName("meta-version")
    val metaVersion: String? = null,
    val author: String? = null,
    val authors: List<String> = listOf(),
    val auth: String? = null,
    val dist: String? = null,
    val depends: List<String> = listOf(),
    @SerialName("build-depends")
    val buildDepends: JsonElement? = null,
    @SerialName("test-depends")
    val testDepends: List<String> = listOf(),
    val provides: Map<String, String> = mapOf(),
    val resources: List<String> = listOf(),
    val documentation: Map<String, String> = mapOf(),
    val support: Support? = null,
    val license: String? = null,
    val tags: List<String> = listOf(),
    val api: String? = null,
    val path: String? = null,
    val production: Boolean? = null,
    val emulates: Map<String, String> = mapOf(),
    val supersedes: Map<String, String> = mapOf(),
    @SerialName("superseded-by")
    val supersededBy: Map<String, String> = mapOf(),
    val excludes: Map<String, String> = mapOf(),
    @SerialName("source-url")
    val sourceUrl: String? = null,
    @SerialName("source-type")
    val sourceType: String? = null,
) {
    val allDepends: List<String>
        get() = listOf(depends, testDepends, simplifiedBuildDepends).flatten()

    val simplifiedBuildDepends: List<String>
        get() = getBuildDepends()

    private fun deconstructBuildDepends(): Any? {
        if (buildDepends == null) return null
        // First try the sane case
        try {
            return Json.decodeFromJsonElement<List<String>>(buildDepends)
        } catch (_: Exception) {}
        // Now try the diabolical case
        try {
            return Json.decodeFromJsonElement<ComplexBuildDepends>(buildDepends)
        } catch (_: Exception) {}
        return null
    }

    @SuppressWarnings("unchecked")
    private fun getBuildDepends(): List<String> {
        val anyBuildDepends = deconstructBuildDepends() ?: return listOf()
        return when (anyBuildDepends) {
            is List<*>              -> anyBuildDepends as List<String>
            is ComplexBuildDepends  -> anyBuildDepends.depends
            else                    -> listOf()
        }
    }
}

// TODO: Decide if/when we ever care about distro-specific dependencies. Right now
// it appears that all such uses are for external libraries.
// This information could still be useful, but we are going to ignore it for now.
@Serializable
data class ComplexBuildDepends(val requires: List<JsonElement>?, val runtime: List<JsonElement>?) {
    val depends: List<String>
        get() = depends()

    private val required: List<JsonElement>
        get() = this.requires ?: listOf()
    private val runtimed: List<JsonElement>
        get() = this.runtime ?: listOf()


    private fun depends(): List<String> {
        return listOf(runtimed, required).flatten().mapNotNull {
            when (it) {
                is JsonObject    -> nullifyNative(it)
                is JsonPrimitive -> if (it.isString) nullifyNative(it.content) else null
                else             -> null
            }
        }
    }

    private fun nullifyNative(maybeNative: JsonObject): String? {
        try {
            val asMap: Map<String, String> = Json.decodeFromJsonElement(maybeNative)
            return if (asMap.keys.size == 1 && asMap.keys.all { it == "name" })
                        nullifyNative(asMap["name"] ?: "")
                    else nullifyNative(Json.decodeFromJsonElement<BuildDependsByDistro>(maybeNative).distroRelevantModule)
        } catch (_: Exception) {}
        return null
    }

    private fun nullifyNative(maybeNative: String): String? {
        if (maybeNative.isEmpty()) return null
        if (Regex(".+:from<native>.*").containsMatchIn(maybeNative)) return null
        return maybeNative
    }
}

@Serializable
data class ComplexBuildDependsElement(val from: String, val name: BuildDependsByDistro)
@Serializable
data class BuildDependsByDistro(
    @SerialName("by-distro.name")
    val byDistroName: Map<String, String>
) {
    val distroRelevantModule: String
        get() = byDistroName[service<RakuDistroInfo>().distroName] ?: ""
}