package org.raku.comma.services

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "Raku.Project.Details", storages = [Storage(value = RakuServiceConstants.PROJECT_SETTINGS_FILE)])
class RakuProjectDetailsService(
    val project : Project
) : PersistentStateComponent<RakudoProjectState> {
    var projectState: RakudoProjectState = determineIfProjectIsRakudoCore()

    override fun getState(): RakudoProjectState { return projectState }
    override fun loadState(state: RakudoProjectState) { this.projectState = state }

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
}

class RakudoProjectState : BaseState() {
    var isProjectRakudoCore by property(false)
}