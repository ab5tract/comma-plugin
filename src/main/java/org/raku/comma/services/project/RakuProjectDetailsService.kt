package org.raku.comma.services.project

import com.intellij.openapi.components.*
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import org.raku.comma.services.RakuServiceConstants
import org.raku.comma.utils.CommaProjectUtil
import java.util.concurrent.atomic.AtomicBoolean

@Service(Service.Level.PROJECT)
@State(name = "Raku.Project.Details", storages = [Storage(value = RakuServiceConstants.PROJECT_SETTINGS_FILE)])
class RakuProjectDetailsService(
    val project: Project,
    private val runScope: CoroutineScope
) : PersistentStateComponent<RakudoProjectState>, DumbAware {
    var projectState: RakudoProjectState = RakudoProjectState()

    override fun getState(): RakudoProjectState {
        return refreshState(projectState)
    }

    override fun loadState(state: RakudoProjectState) {
        this.projectState = refreshState(state)
    }

    // moduleServiceStatus
    // Tracks whether the module details and dependencies have been loaded. Reset prior to refreshing these services.
    private val moduleServiceStatus = AtomicBoolean(false)
    var moduleServiceDidStartup: Boolean
        get() = moduleServiceStatus.get()
        set(value) = moduleServiceStatus.set(value)
    val noModuleServiceDidNotStartup: Boolean
        get() = !moduleServiceDidStartup

    // missingDependenciesNotificationStatus
    // Tracks whether we have already prompted about missing dependencies. This avoids a pile-up of notification prompts.
    private val missingDependencyNotificationStatus = AtomicBoolean(false)
    var hasNotifiedMissingDependencies: Boolean
        get() = missingDependencyNotificationStatus.get()
        set(value) = missingDependencyNotificationStatus.set(value)

    // projectFilesScannedStatus
    // Tracks whether the project files have been scanned for Raku files and Rakudo core status.
    private val projectFilesScannedStatus = AtomicBoolean(false)
    var hasScannedForRakuFiles: Boolean
        get() = projectFilesScannedStatus.get()
        set(value) = projectFilesScannedStatus.set(value)

    // projectSdkPromptedStatus
    // Tracks whether the SDK prompt has already been shown to the user. This allows 'Cancel' to be
    // meaningfully selected without resulting in the popup repeatedly appearing as a result.
    private val projectSdkPromptedStatus = AtomicBoolean(false)
    var hasProjectSdkPrompted: Boolean
        get() = projectSdkPromptedStatus.get()
        set(value) = projectSdkPromptedStatus.set(value)

    // Detail:  isProjectProjectRakudoCore
    // Purpose: To allow toggling certain annotations and other features that make sense in regular
    //          Raku projects but which make hacking on the Rakudo internals annoying.
    val isProjectRakudoCore: Boolean
        get() = state.isProjectRakudoCore

    private fun determineIfProjectIsRakudoCore(): Boolean {
        return project.name == "rakudo" || project.basePath?.endsWith("rakudo") == true
    }

    // Detail:  doesProjectContainRakuCode
    // Purpose: Suppress all Raku-related services unless the project actually contains Raku files
    val doesProjectContainRakuCode: Boolean
        get() = state.doesProjectContainRakuCode

    private fun refreshState(state: RakudoProjectState): RakudoProjectState {
        if (!hasScannedForRakuFiles) {
            state.doesProjectContainRakuCode = CommaProjectUtil.projectContainsRakuCode(project)
            state.isProjectRakudoCore = determineIfProjectIsRakudoCore()
        }
        hasScannedForRakuFiles = true
        return state
    }
}

class RakudoProjectState : BaseState() {
    var doesProjectContainRakuCode by property(false)
    var isProjectRakudoCore by property(false)
}