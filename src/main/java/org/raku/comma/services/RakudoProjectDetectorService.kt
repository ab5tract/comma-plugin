package org.raku.comma.services

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "Raku.Project.RakudoCore", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class RakudoProjectDetectorService(
    val project : Project
) : PersistentStateComponent<RakudoProjectState> {
    var projectState: RakudoProjectState = determineState()

    private fun determineState(): RakudoProjectState {
        val coreCheck = project.name == "rakudo" || project.basePath?.endsWith("rakudo") == true
        val thisState = RakudoProjectState()
        thisState.isProjectRakudoCore = coreCheck
        return thisState
    }

    override fun getState(): RakudoProjectState { return projectState }
    override fun loadState(state: RakudoProjectState) { this.projectState = state }

    fun isProjectRakudo() : Boolean {
        return projectState.isProjectRakudoCore
    }
}

class RakudoProjectState : BaseState() {
    var isProjectRakudoCore by property(false)
}