package org.raku.comma.services.application

import com.intellij.execution.ExecutionException
import com.intellij.openapi.components.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import kotlinx.coroutines.withContext
import org.raku.comma.services.RakuServiceConstants
import org.raku.comma.utils.RakuCommandLine
import org.raku.comma.utils.RakuUtils

@Service(Service.Level.APP)
@State(name = "Raku.Distro", storages = [ Storage(RakuServiceConstants.APP_RAKU_DISTRO, roamingType = RoamingType.DISABLED) ])
class RakuDistroInfo(val runScope: CoroutineScope) : PersistentStateComponent<RakuDistroInfoState> {
    private var distroState = determineDistroName()

    val distroName: String
        get() = distroState.distroName ?: ""

    private fun determineDistroName(): RakuDistroInfoState {
        return runScope.future {
            withContext(Dispatchers.IO) {
                val sdkName: String = service<RakuSdkStore>().sdks.firstOrNull()?.path ?: return@withContext RakuDistroInfoState()
                val commandLine = RakuCommandLine(sdkName)
                val distroScript = RakuUtils.getResourceAsFile("scripts/get-distro.raku")
                                        ?: throw ExecutionException("Resource bundle is corrupted: get-distro.raku script is missing")
                val distro: String = commandLine.executeAndRead(distroScript).firstOrNull() ?: return@withContext RakuDistroInfoState()
                val distroInfo = RakuDistroInfoState()
                distroInfo.distroName = distro
                distroInfo
            }
        }.join()
    }


    override fun getState(): RakuDistroInfoState {
        return distroState
    }

    override fun loadState(state: RakuDistroInfoState) {
        distroState = state
    }
}

class RakuDistroInfoState : BaseState() {
    var distroName by string()
}