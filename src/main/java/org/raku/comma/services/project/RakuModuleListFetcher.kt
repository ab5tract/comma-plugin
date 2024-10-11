package org.raku.comma.services.project

import com.intellij.openapi.components.*
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.platform.ide.progress.withModalProgress
import com.intellij.platform.util.progress.ProgressReporter
import com.intellij.platform.util.progress.reportProgress
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json.JSONException
import org.raku.comma.metadata.data.MetaFile
import org.raku.comma.services.RakuServiceConstants
import org.raku.comma.utils.RakuUtils
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap


// TODO: Migrate this to an APP service
@Service(Service.Level.PROJECT)
@State(name = "Raku.Modules.Available", storages = [Storage(value = RakuServiceConstants.RAKU_EXTERNAL_MODULES_FILE)])
class RakuModuleListFetcher(
    private val project: Project,
    private val runScope: CoroutineScope
) : PersistentStateComponent<MetaFileRepositoryState>, DumbAware {

    var metaFileState: MetaFileRepositoryState = MetaFileRepositoryState()

    val GITHUB_MIRROR1: String = "https://raw.githubusercontent.com/ugexe/Perl6-ecosystems/master/p6c1.json"

    //    public static final String CPAN_MIRROR1 = "https://raw.githubusercontent.com/ugexe/Perl6-ecosystems/master/cpan1.json";
    val FEZ_MIRROR1: String = "https://360.zef.pm/"
    val REA_MIRROR1: String = "https://raw.githubusercontent.com/Raku/REA/main/META.json"

    private data class Repo(val name: String, val url: String)

    private var metaFileRepository: Map<String, MetaFile> = mapOf()

    private val repos: List<Repo> = listOf(
        Repo("github", GITHUB_MIRROR1),
        Repo("fez", FEZ_MIRROR1),
        Repo("rea", REA_MIRROR1)
    )

    val PREINSTALLED_MODULES: List<String> = persistentListOf(
        "CompUnit::Repository::Staging",
        "CompUnit::Repository::FileSystem",
        "CompUnit::Repository::Installation",
        "CompUnit::Repository::AbsolutePath",
        "CompUnit::Repository::Unknown",
        "CompUnit::Repository::NQP",
        "CompUnit::Repository::Raku",
        "CompUnit::Repository::RepositoryRegistry",
        "NativeCall",
        "NativeCall::Types",
        "NativeCall::Compiler::GNU",
        "NativeCall::Compiler::MSVC",
        "Test", "Pod::To::Text", "Telemetry"
    )
    val PRAGMAS: List<String> = persistentListOf(
        "v6.c", "v6.d", "v6.e", "MONKEY-GUTS", "MONKEY-SEE-NO-EVAL",
        "MONKEY-TYPING", "MONKEY", "experimental", "fatal",
        "internals", "invocant", "isms", "lib", "nqp", "newline",
        "parameters", "precompilation", "soft", "strict",
        "trace", "v6", "variables", "worries"
    )

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private var isGettingReady = false
    var isReady: Boolean = false

    val isNotReady: Boolean
        get() = !isReady

    val isNotGettingReady: Boolean
        get() = !isGettingReady

    val moduleList: List<MetaFile>
        get() = metaFileRepository.values.filter { it.name != null }.toList()

    val moduleNames: Set<String>
        get() = getNames()

    fun repository(): Map<String, MetaFile> {
        return metaFileRepository
    }

    fun getModuleByProvide(provide: String?): String? {
        return state.providedToModule[provide]
    }

    fun getProvidesMapByModule(name: String): Map<String, String> {
        val module = repository()[name] ?: return mapOf()
        return module.provides
    }

    fun getProvidesListByModule(name: String): List<String> {
        return state.moduleToProvides[name] ?: listOf()
    }

    fun getProvidesListByModules(modules: List<String>): List<String> {
        val output = mutableListOf<String>()
        modules.forEach { output.addAll(getProvidesListByModule(it)) }
        return output
    }

    fun getNames(): Set<String> {
        return moduleList.map { it.name!! }.map(RakuUtils::stripAuthVerApi).toSet()
    }

    fun getProvides(): Set<String> {
        return moduleList.filter { it.provides.isNotEmpty() }
                         .flatMap { it.provides.values }
                         .toSet()
    }

    fun dependenciesDeep(modules: Set<String>): Set<String> {
        val find = { moduleSet: Set<String> ->
            moduleList.filter { moduleSet.contains(it.name) }
                      .flatMap { it.depends.map(RakuUtils::stripAuthVerApi) }
                      .toMutableSet()
        }

        val seen = find(modules)
        var lookup = seen - modules

        while (lookup.isNotEmpty()) {
            val thisRound = lookup // just to keep it a bit more readable
            seen.addAll(thisRound)
            lookup = find(thisRound) - seen
        }

        return seen.toSet()
    }

    private suspend fun populateModules(): Map<String, MetaFile> {
        if (isNotGettingReady) {
            if (isNotReady) {
                isGettingReady = true
                return runScope.async {
                    val cache = populateModulesList()
                    project.getService(RakuProjectDetailsService::class.java).setEcosystemModuleRefresh()
                    isReady = true
                    isGettingReady = false
                    cache
                }.await()
            } else {
                return metaFileRepository
            }
        } else {
            return mapOf()
        }
    }

    private fun populateModulesList(): MutableMap<String, MetaFile> {
        val metaFiles: MutableList<MetaFile> = mutableListOf()
        // TODO: Create a configuration that searches more ecosystems
        for (repo in repos) {
            val output = doRequest(repo.url)
            if (output != null) {
                metaFiles.addAll(populateArrayFromSource(output))
            }
        }

        val modulesMap: MutableMap<String, MetaFile> = ConcurrentHashMap()
        for (metaFile in metaFiles) {
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

    private fun checkVersions(module: MetaFile, modulesMap: Map<String, MetaFile>): Boolean {
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

    private fun populateArrayFromSource(output: String): List<MetaFile> {
        try {
            val fullArray = Json.decodeFromString<JsonArray>(output)

            val exceptions = ArrayList<Exception>()
            val repo: List<MetaFile> = fullArray.map { element ->
                try {
                    json.decodeFromJsonElement<MetaFile>(element)
                } catch (ignore: Exception) {
                    exceptions.add(ignore)
                    null
                }
            }.filterNotNull().toList()

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

    private suspend fun fillState(report: ProgressReporter) {
        // TODO: Is there a cleaner way to do this than a completable?
        val repo = report.sizedStep(20, "Fetching ecosystem details") { populateModules() }
        metaFileRepository = repo

        // Don't bother with any additional processing if nothing has changed
        if (metaFileState.moduleNames.sorted() == repo.keys.stream().sorted().toList()) return

        metaFileState.moduleNames = repo.keys.stream().toList()

        val duplicates = mutableSetOf<String>()
        val providedToPath: MutableMap<String, String> = mutableMapOf()
        repo.entries.flatMap { it.value.provides.entries }.forEach { entry ->
            if (providedToPath[entry.key] == null) {
                providedToPath[entry.key] = entry.value
            } else {
                duplicates.add(entry.key)
            }
        }
        metaFileState.providedToPath = providedToPath

        val providedToModule: MutableMap<String, String> = mutableMapOf()
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
        metaFileState.providedToModule = providedToModule
        metaFileState.moduleToProvides = moduleList.associate { module ->
            Pair(module.name!!, module.provides.keys.toList())
        }.toMutableMap()
    }

    override fun getState(): MetaFileRepositoryState {
        val shouldRefresh = project.getService(RakuProjectDetailsService::class.java).needsEcosystemModuleRefresh()
                                || metaFileRepository.isEmpty()
        if (shouldRefresh && isNotReady && isNotGettingReady) {
            runScope.launch {
                withBackgroundProgress(project, "Loading ecosystem information...") { reportProgress { report -> fillState(report) } }
            }
        }
        return metaFileState
    }

    override fun loadState(state: MetaFileRepositoryState) {
        metaFileState = state
    }
}

@Serializable
data class ProvideReference(
    val provideReference: String,
    val module: String? = null,
    val relativePath: String? = null,
    val absoluteFilePath: String? = null
)

class MetaFileRepositoryState : BaseState() {
    // TODO: Fix serialization .. currently it dies when trying to deserialize metas with a single string authors value :
    //    var metaFileRepository by map<String, MetaFile>()
    var providedToPath by map<String, String>()
    var providedToModule by map<String, String>()
    var moduleToProvides by map<String, List<String>>()
    var moduleNames by list<String>()
}