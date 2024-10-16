package org.raku.comma.services.project

import com.intellij.openapi.components.*
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import kotlinx.datetime.*
import org.raku.comma.services.RakuServiceConstants
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration

@Service(Service.Level.PROJECT)
@State(name = "Raku.Project.Details", storages = [Storage(value = RakuServiceConstants.PROJECT_SETTINGS_FILE)])
class RakuProjectDetailsService(
    val project : Project
) : PersistentStateComponent<RakudoProjectState>, DumbAware {
    var projectState: RakudoProjectState = determineIfProjectIsRakudoCore()

    override fun getState(): RakudoProjectState { return projectState }
    override fun loadState(state: RakudoProjectState) { this.projectState = state }

    private val moduleServiceStatus = AtomicBoolean(false)
    var moduleServiceDidStartup: Boolean
        get() = moduleServiceStatus.get()
        set(value) = moduleServiceStatus.set(value)
    val noModuleServiceDidNotStartup: Boolean
        get() = !moduleServiceDidStartup

    // Detail:  isProjectProjectRakudoCore
    // Purpose: To allow toggling certain annotations and other features that make sense in regular
    //          Raku projects but which make hacking on the Rakudo internals annoying.
    fun isProjectRakudoCore() : Boolean { return projectState.isProjectRakudoCore }
    private fun determineIfProjectIsRakudoCore(): RakudoProjectState {
        val coreCheck = project.name == "rakudo" || project.basePath?.endsWith("rakudo") == true
        val thisState = RakudoProjectState()
        thisState.isProjectRakudoCore = coreCheck
        return thisState
    }

    fun needsEcosystemModuleRefresh(): Boolean {
        if (projectState.lastEcosystemModuleRefresh.isNullOrBlank()) return true

        val lastRefresh: Instant = Instant.parse(projectState.lastEcosystemModuleRefresh!!)
        return lastRefresh.plus(Duration.parse("45m")) < Clock.System.now()
    }

    fun setEcosystemModuleRefresh() {
        projectState.lastEcosystemModuleRefresh = Clock.System.now().toString()
    }
}

class RakudoProjectState : BaseState() {
    var isProjectRakudoCore by property(false)
    var lastEcosystemModuleRefresh by string()
}