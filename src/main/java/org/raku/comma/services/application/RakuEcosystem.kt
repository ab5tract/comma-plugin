package org.raku.comma.services.application

import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.raku.comma.metadata.ExternalMetaFile
import org.raku.comma.services.support.moduleDetails.ModuleListFetcher
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

@Service(Service.Level.APP)
class RakuEcosystem(private val runScope: CoroutineScope) {

    val moduleListFetcher = ModuleListFetcher(runScope)

    private val initializationFuture = CompletableFuture<EcosystemDetailsState>()
    val isInitialized: Boolean
        get() = initializationFuture.isDone
    val isNotInitialized: Boolean get() = !isInitialized

    private val initializationStatus = AtomicBoolean(false)
    var isInitializing: Boolean
        get() = initializationStatus.get()
        set(new) = initializationStatus.set(new)
    val isNotInitializing: Boolean
        get() = !isInitializing

    // Load the data on service start
    private var ecosystemState = initialize().join()
    val ecosystem: EcosystemDetailsState
        get() = ecosystemState

    fun initialize(): CompletableFuture<EcosystemDetailsState> {
        if (isNotInitializing && isNotInitialized) {
            isInitializing = true
            runScope.launch {
                ecosystemState = moduleListFetcher.fillState(EcosystemDetailsState())
                initializationFuture.complete(ecosystemState.copy())
                isInitializing = false
            }
            return initializationFuture
        } else {
            return if (isInitializing) initializationFuture else CompletableFuture.completedFuture(ecosystemState)
        }
    }
}

data class EcosystemDetailsState(
    val ecoProvideToPath: Map<String, String> = mapOf(),
    val ecoProvideToModule: Map<String, String> = mapOf(),
    val ecoModuleToProvides: Map<String, List<String>> = mapOf(),

    val ecosystemRepository: Map<String, ExternalMetaFile> = mapOf(),
    val moduleNames: List<String> = listOf(),
    val metaFiles: List<ExternalMetaFile> = listOf(),
)
