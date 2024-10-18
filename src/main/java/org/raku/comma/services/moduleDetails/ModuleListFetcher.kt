package org.raku.comma.services.moduleDetails

import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.future
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json.JSONException
import org.raku.comma.metadata.data.ExternalMetaFile
import org.raku.comma.services.ModuleDetailsState
import org.raku.comma.utils.CommaProjectUtil
import org.raku.comma.utils.RakuUtils
import java.io.IOException
import java.util.Map.copyOf
import java.util.concurrent.ConcurrentHashMap

class ModuleListFetcher(private val project: Project, private val runScope: CoroutineScope) {

    val GITHUB_MIRROR1: String = "https://raw.githubusercontent.com/ugexe/Perl6-ecosystems/master/p6c1.json"

    // TODO: Deal with this nearly unparseable monster!
    //    public static final String CPAN_MIRROR1 = "https://raw.githubusercontent.com/ugexe/Perl6-ecosystems/master/cpan1.json";
    val FEZ_MIRROR1: String = "https://360.zef.pm/"
    val REA_MIRROR1: String = "https://raw.githubusercontent.com/Raku/REA/main/META.json"

    private data class Repo(val name: String, val url: String)

    private val repos: List<Repo> = listOf(
        Repo("github", GITHUB_MIRROR1),
        Repo("fez", FEZ_MIRROR1),
        Repo("rea", REA_MIRROR1)
    )

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private suspend fun populateModules(): Map<String, ExternalMetaFile> {
        return runScope.async {
            populateModulesList()
        }.await()
    }

    private fun populateModulesList(): MutableMap<String, ExternalMetaFile> {
        val externalMetaFiles: MutableList<ExternalMetaFile> = mutableListOf()
        // TODO: Create a configuration that searches more ecosystems
        for (repo in repos) {
            val output = doRequest(repo.url)
            if (output != null) {
                externalMetaFiles.addAll(populateArrayFromSource(output))
            }
        }

        val modulesMap: MutableMap<String, ExternalMetaFile> = ConcurrentHashMap()
        for (metaFile in externalMetaFiles) {
            if (modulesMap[metaFile.name!!] == null || checkVersions(metaFile, modulesMap)) {
                // There is a Foo::[]Foo module which contains an illegal character that will blow us up when serializing to XML
                if (! (metaFile.name.startsWith("Foo") && metaFile.name.endsWith("Foo"))) {
                    val name = RakuUtils.stripAuthVerApi(metaFile.name)
                    modulesMap[name] = metaFile
                }
            }
        }
        return modulesMap
    }

    private fun checkVersions(module: ExternalMetaFile, modulesMap: Map<String, ExternalMetaFile>): Boolean {
        // Do not add if invalid, in any case
        val versionNew = module.version
        val versionBefore = modulesMap[module.name!!]?.version

        if (versionNew == null && versionBefore == null) return true
        if (versionNew == null) return false
        if (versionBefore == null) return true

        val version1 = versionBefore.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val version2 = versionNew.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        var i = 0
        // set index to first non-equal ordinal or length of shortest version string
        while (i < version1.size && i < version2.size && version1[i] == version2[i]) {
            i++
        }
        // compare first non-equal ordinal number
        if (i < version1.size && i < version2.size) {
            return try {
                version1[i].toInt() < version2[i].toInt()
            } catch (e: Exception) {
                true
            }
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return version1.size < version2.size
    }

    private fun populateArrayFromSource(output: String): List<ExternalMetaFile> {
        try {
            val fullArray = Json.decodeFromString<JsonArray>(output)

            val exceptions = ArrayList<Exception>()
            val repo: List<ExternalMetaFile> = fullArray.mapNotNull { element ->
                try {
                    json.decodeFromJsonElement<ExternalMetaFile>(element)
                } catch (ignore: Exception) {
                    exceptions.add(ignore)
                    null
                }
            }.toList()

            return repo
        } catch (e: JSONException) {
            throw e
        }
    }

    private fun doRequest(url: String): String? {
        try {
            HttpClients.createDefault().use { client ->
                val httpGet = HttpGet(url)
                try {
                    val response: HttpResponse = client.execute(httpGet)
                    if (response.statusLine.statusCode != 200) return null
                    if (response.entity == null) return null
                    return EntityUtils.toString(response.entity)
                } catch (e: Exception) {
                    return null
                }
            }
        } catch (e: IOException) {
            return null
        }
    }

    @Synchronized
    fun fillState(state: ModuleDetailsState): ModuleDetailsState {
        val externalMetaFileRepository = state.ecosystemRepository.ifEmpty {
            runScope.future {
                populateModules()
            }.join()
        }
        val moduleNames = externalMetaFileRepository.keys.toList()

        val duplicates = mutableSetOf<String>()
        val providedToPath: MutableMap<String, String> = mutableMapOf()
        externalMetaFileRepository.entries.flatMap { it.value.provides.entries }.forEach { entry ->
            if (providedToPath[entry.key] == null) {
                providedToPath[entry.key] = entry.value
            } else {
                duplicates.add(entry.key)
            }
        }
        val ecoProvideToPath = copyOf(providedToPath)

        val providedToModule: MutableMap<String, String> = mutableMapOf()
        val moduleList: List<ExternalMetaFile> = externalMetaFileRepository.values.toList()
        moduleList.flatMap { module ->
            module.provides.entries.map {
                ProvideReference(
                    it.key,
                    module.name,
                    relativePath = it.value,
                    absoluteFilePath = module.path
                )
            }
        }.forEach { provider ->
            if (providedToModule[provider.provideReference] == null && provider.module != null) {
                providedToModule[provider.provideReference] = RakuUtils.stripAuthVerApi(provider.module)
            }
        }
        val ecoModuleToProvides = moduleList.associate { module ->
            Pair(module.name!!, module.provides.keys.toList())
        }


        return state.copy(moduleNames = moduleNames,
                          metaFiles = moduleList,
                          ecosystemRepository = externalMetaFileRepository,
                          ecoProvideToPath = ecoProvideToPath,
                          ecoModuleToProvides = ecoModuleToProvides,
                          ecoProvideToModule = providedToModule)
    }
}

private data class ProvideReference(
    val provideReference: String,
    val module: String? = null,
    val relativePath: String? = null,
    val absoluteFilePath: String? = null
)