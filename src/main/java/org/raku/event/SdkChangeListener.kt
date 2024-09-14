package org.raku.event

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.raku.metadata.RakuMetaDataComponent
import org.raku.sdk.RakuSdkType


@Service(Service.Level.PROJECT)
class SdkChangeListener(private val myProject: Project) {
    fun projectSdkChanged(sdk: Sdk?) {
        if (!ApplicationManager.getApplication().isUnitTestMode) {
            RakuSdkType.getInstance().invalidateCaches(myProject)
            for (module in ModuleManager.getInstance(myProject).modules) {
                val component = module.getService(RakuMetaDataComponent::class.java)
                component?.triggerMetaBuild()
            }
        }
    }
}
