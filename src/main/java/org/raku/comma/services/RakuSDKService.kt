package org.raku.comma.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.*
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import org.raku.comma.metadata.RakuMetaDataComponent
import org.raku.comma.sdk.RakuSdkType

/**
 * This service provides means to work with "secondary SDK".
 * When the Raku plugin is used in products such as IDEA or Webstorm,
 * setting Raku SDK is either impossible or interferes with main project code.
 * To workaround this, we provide some UI to the user to set specific Raku SDK
 * for a project. This service maintains state of this SDK and can be used to set/obtain
 * such "secondary" SDK for Raku parts.
 */
@Service(Service.Level.PROJECT)
@State(name = "Raku.SDK", storages = [Storage(value = RakuServiceConstants.PROJECT_SETTINGS_FILE)])
class RakuSDKService(private val myProject: Project) : PersistentStateComponent<RakuSDKState> {
    var myRakuSDKState: RakuSDKState = RakuSDKState()

    override fun getState(): RakuSDKState {
        return myRakuSDKState
    }

    override fun loadState(rakuSDKState: RakuSDKState) {
        myRakuSDKState = rakuSDKState
    }

    fun setProjectSdkPath(sdkPath: String) {
        myRakuSDKState.projectSdkPath = sdkPath

        val instance = RakuSdkType.getInstance()
        val versionString = instance.getVersionString(sdkPath)

        // If this Sdk is not registered in global project jdk table, we have to add that
        // so that project-less things like Raku VFS still could find them.
        if (versionString != null) {
            val sdk = ProjectJdkTable.getInstance().findJdk(versionString)
            if (sdk == null) {
                val secondarySdk = ProjectJdkTable.getInstance().createSdk(versionString, instance)
                WriteAction.run<RuntimeException> {
                    ProjectJdkTable.getInstance().addJdk(secondarySdk)
                }
            }
        }

        // The secondary SDK was set, invalidate caches
        instance.invalidateCaches(myProject)
        for (module in ModuleManager.getInstance(myProject).modules) {
            val component = module.getService(RakuMetaDataComponent::class.java)
            component?.triggerMetaBuild()
        }
    }

    fun getProjectSdkPath(): String? {
        return myRakuSDKState.projectSdkPath
    }

    fun getProjectSdkName(): String? {
        return RakuSdkType.suggestSdkName(state.projectSdkPath!!)
    }
}

class RakuSDKState : BaseState() {
    var projectSdkPath by string()
}