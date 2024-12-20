package org.raku.comma.services.application

import com.intellij.openapi.components.*
import org.raku.comma.sdk.RakuSdkUtil
import org.raku.comma.services.RakuServiceConstants
import java.nio.file.Path
import kotlin.io.path.exists


@Service(Service.Level.APP)
@State(name = "Raku.Sdks.Available", storages = [ Storage(RakuServiceConstants.APP_RAKU_SDK_OPTIONS, roamingType = RoamingType.DISABLED) ])
class RakuSdkStore : PersistentStateComponent<SdkStoreState> {
    private var sdkStore = SdkStoreState()

    val sdks: List<RakuSdkStoreEntry>
        get() = state.sdks

    fun addSdk(path: String): RakuSdkStoreEntry {
        val version = RakuSdkUtil.versionString(path)
                        ?: throw RuntimeException("Unable to determine version string for Raku SDK path '$path'")
        val zefPath = Path.of(path).parent.resolve("share/perl6/site/bin/zef")
        val zef = if (zefPath.exists()) zefPath.toString() else ""
        return addSdk(path, version, zef)
    }

    fun addSdk(path: String, version: String, zef: String): RakuSdkStoreEntry {
        val maybeReplace = sdks.firstOrNull { it.path == path }

        var newSdk: RakuSdkStoreEntry? = null
        val newSdks: MutableList<RakuSdkStoreEntry> = mutableListOf()

        if (maybeReplace != null) {
            for ((idx, sdk) in sdks.withIndex()) {
                newSdk = if (maybeReplace == sdk) RakuSdkStoreEntry(path, version, idx, zef) else sdk.copy(index = idx, zef = zef)
                newSdks.add(newSdk)
                sdkStore.paths.add(idx, newSdk.path)
                sdkStore.versions.add(idx, newSdk.version)
                sdkStore.zef.add(idx, newSdk.zef)
            }
        } else {
            val idx = sdks.size
            newSdk = RakuSdkStoreEntry(path, version, idx, zef)
            sdkStore.paths.add(idx, path)
            sdkStore.versions.add(idx, version)
            sdkStore.zef.add(idx, zef)
        }

        return newSdk!!
    }

    override fun getState(): SdkStoreState {
        return sdkStore
    }

    override fun loadState(newState: SdkStoreState) {
        sdkStore = newState
    }
}

data class RakuSdkStoreEntry(
    var path: String,
    var version: String,
    val index: Int,
    var zef: String = ""
) { override fun toString(): String { return "Raku SDK $version" } }

class SdkStoreState : BaseState() {
    var paths by list<String>()
    var versions by list<String>()
    var zef by list<String>()

    val sdks: List<RakuSdkStoreEntry>
        get() = paths.zip(versions.zip(zef)).withIndex().map { (index, sdk) ->
            RakuSdkStoreEntry(sdk.first, sdk.second.first, index, sdk.second.second)
        }
}